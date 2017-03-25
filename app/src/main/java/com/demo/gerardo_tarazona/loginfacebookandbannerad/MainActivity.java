package com.demo.gerardo_tarazona.loginfacebookandbannerad;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private CallbackManager cM;
    private LoginButton lB;
    private AdView adView;
    private TextView welcome;
    private ImageView profilePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cM = CallbackManager.Factory.create();

        getFBKeyHash("FrCaf8NxQkGWBMYD2NsqKSRXuX8=");


        setContentView(R.layout.activity_main);
        lB = (LoginButton) findViewById(R.id.login_facebook);
        welcome = (TextView) findViewById(R.id.welcome);
        profilePhoto = (ImageView) findViewById(R.id.imageView);
        profilePhoto.setVisibility(ImageView.INVISIBLE);
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                if (currentAccessToken == null) {

                    welcome.setText("");
                    profilePhoto.setVisibility(ImageView.INVISIBLE);

                }
            }
        };


        lB.registerCallback(cM, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(MainActivity.this, "Inicio de sesion exitoso", Toast.LENGTH_SHORT).show();
                profilePhoto.setVisibility(ImageView.VISIBLE);

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object,GraphResponse response) {
                                // Application code
                                try {
                                    welcome.setText("Bienvenido "+object.getString("name")+"\n"+object.get("email"));
                                    Profile profile = Profile.getCurrentProfile();


                                    //Log.e("Image URI", "--" +  profile.getProfilePictureUri(profilePhoto.getMaxWidth(),profilePhoto.getMaxHeight()).toString());
                                    ImageLoadTask imageLoadTask = new ImageLoadTask(profile.getProfilePictureUri(
                                            profilePhoto.getMaxWidth(),
                                            profilePhoto.getMaxHeight()).toString(),
                                            profilePhoto);
                                    imageLoadTask.execute();




                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,gender,birthday,email");
                request.setParameters(parameters);
                request.executeAsync();



            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "inicio de sesion cancelado", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this, "Inicio de sesion no exitoso", Toast.LENGTH_SHORT).show();



            }

        });
        adView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder().setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

    }

    private void getFBKeyHash(String packageName) {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            for(Signature signature:info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                Log.d("keyHash:", Base64.encodeToString(md.digest(),Base64.DEFAULT));
                System.out.println("keyHash: "+Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int reqCode,int resCode,Intent i){
        cM.onActivityResult(reqCode,resCode,i);
    }
    @Override
    protected void onDestroy() {
        if(adView!=null){
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if(adView!=null){
            adView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(adView!=null){
            adView.resume();
        }
        super.onResume();
    }


}
