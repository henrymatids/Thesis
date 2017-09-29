package com.example.henrymatidios.thesis;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    // UI references.
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Firebase
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);
        // Set up the login form.
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                updateUI(user);
            }
        };

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                return id == R.id.login || id == EditorInfo.IME_NULL;
            }
        });

        Button mSignInButton= (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(this);

        mUsernameView.setError(null);
        mPasswordView.setError(null);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        //Checks if the user is signed in or not;
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();
        updateUI(mCurrentUser);
    }

    @Override
    public  void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected  void onPause() {
        super.onPause();
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();

        if(i == R.id.sign_in_button) {
            String email = mUsernameView.getText().toString();
            String password = mPasswordView.getText().toString();

            if(!(TextUtils.isEmpty(email) && TextUtils.isEmpty(password))) {
                //if the fields are not empty
//                startActivity(new Intent(LoginActivity.this, LandingPageActivity.class));
//                finish();
                signIn(email, password);
            } else {
                //if fields are empty. display error messages
                if(TextUtils.isEmpty(email)) {
                    mUsernameView.setError(getString(R.string.error_field_required));
                }

                if(TextUtils.isEmpty(password)) {
                    mPasswordView.setError(getString(R.string.error_field_required));
                }
            }
        }
    }

    private void signIn(String email, String password) {

        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    startService(new Intent(getApplication(), NotificationService.class));
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthInvalidUserException e) {
                                        //email does not exist.
                                        mUsernameView.setError(getString(R.string.error_username_or_password));
                                        mUsernameView.requestFocus();
                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                        //wrong password.
                                        mPasswordView.setError(getString(R.string.error_username_or_password));
                                    } catch (Exception e) {
                                        Log.e("createAccount Error:", e.getMessage());
                                    }

                                    updateUI(null);
                                    Toast.makeText(getApplicationContext(),"Network connection failed!", Toast.LENGTH_LONG).show();
                                }
                                hideProgressDialog();
                            }
                        }
                );
    }

    /**
     * Changes the UI
     * @param user current user status. Has a Null value if there are no user logged in.
     */
    private void updateUI(FirebaseUser user) {
        if(user != null){
            //check the user credentials
            startActivity(new Intent(LoginActivity.this, LandingPageActivity.class));
            finish();
        }
    }

}

