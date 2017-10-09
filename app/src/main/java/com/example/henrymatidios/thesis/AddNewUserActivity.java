package com.example.henrymatidios.thesis;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddNewUserActivity extends BaseActivity implements View.OnClickListener {

    //UI variables
    private EditText mFirstname;
    private EditText mLastname;
    private EditText mPassword;
    private EditText mUsername;
    private Spinner mConsoleNode;
    private Spinner mType;

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private ValueEventListener valueEventListener;

    private List<String> accountTypes = new ArrayList<>();
    private List<String> mConsoleNodeValues = new ArrayList<>();
    private String accType;
    private String node;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_user);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_add_new_user);
        setSupportActionBar(toolbar);
        //UI instances
        Button mAddButton = (Button) findViewById(R.id.add_new_user_button);
        mUsername = (EditText) findViewById(R.id.add_new_user_editText_username);
        mPassword = (EditText) findViewById(R.id.add_new_user_editText_password);
        mFirstname = (EditText) findViewById(R.id.add_new_user_editText_firstname);
        mLastname = (EditText) findViewById(R.id.add_new_user_editText_lastname);

        //Firebase instances
        mAuth = FirebaseAuth.getInstance();
        dbRef = Utils.getDatabase(true).getReference();

        accountTypes.add("Admin");
        accountTypes.add("User");

        //Spinner
        mType = (Spinner) findViewById(R.id.account_type);
        mConsoleNode = (Spinner) findViewById(R.id.console_node);

        CustomAdapter spinnerAdapter = new CustomAdapter(AddNewUserActivity.this, accountTypes);
        mType.setAdapter(spinnerAdapter);

        getConsoleNodesFromFirebase();
        mType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                accType = parent.getItemAtPosition(position).toString();

                switch (position) {
                    case 0:
                        mConsoleNode.setVisibility(View.GONE);
                        break;
                    case 1:
                        mConsoleNode.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mConsoleNode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                node = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
            backButton.setOnClickListener(this);
        }
        mAddButton.setOnClickListener(this);

        //Reset all the fields
        mUsername.setError(null);
        mPassword.setError(null);
        mFirstname.setError(null);
        mLastname.setError(null);
        mType.setSelection(0);
        mConsoleNode.setSelection(0);
        mConsoleNode.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View view) {

        int i = view.getId();

        if (i == R.id.add_new_user_button) {
            String email = mUsername.getText().toString();
            String password = mPassword.getText().toString();
            String firstname = mFirstname.getText().toString();
            String lastname = mLastname.getText().toString();

            //CHECK IF THE FIELDS ARE EMPTY
            if (!(TextUtils.isEmpty(email) && TextUtils.isEmpty(password))) {
                createAccount(email, password, accType, firstname + " " + lastname);
            } else {
                if (TextUtils.isEmpty(email)) {
                    mUsername.setError(getString(R.string.error_field_required));
                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError(getString(R.string.error_field_required));
                }

                if (TextUtils.isEmpty(firstname)) {
                    mFirstname.setError(getString(R.string.error_field_required));
                }

                if (TextUtils.isEmpty(lastname)) {
                    mLastname.setError(getString(R.string.error_field_required));
                }
            }
        } else if (i == R.id.back_button) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbRef.removeEventListener(valueEventListener);
    }
    /**
     * Creates new account and stores to the database.
     * @param email    email
     * @param password password
     * @param type     account type
     * @param name     account name
     */
    private void createAccount(final String email, final String password, final String type, final String name) {

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI
                            Map<String, String> mapType = new HashMap<>();
                            mapType.put("name", name.toUpperCase());
                            mapType.put("type", type);
                            if (type.equals("1")) {
                                mapType.put("console", mConsoleNodeValues.get(Integer.parseInt(node)));
                            }

                            dbRef.child("Accounts").child(task.getResult().getUser().getUid()).setValue(mapType);

                            Toast.makeText(AddNewUserActivity.this, "Successfully Added", Toast.LENGTH_LONG).show();

                            mUsername.setText("");
                            mPassword.setText("");
                            mFirstname.setText("");
                            mLastname.setText("");
                            mType.setSelection(0);
                            mConsoleNode.setSelection(0);
                            mConsoleNode.setVisibility(View.GONE);

                        } else {
                            // If sign in fails.
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                //weak password
                                mPassword.setError(getString(R.string.error_weak_password));
                                mPassword.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                //invalid email
                                mUsername.setError(getString(R.string.error_invalid_email));
                                mUsername.requestFocus();
                            } catch (FirebaseAuthUserCollisionException e) {
                                //user already exist
                                mUsername.setError(getString(R.string.error_user_exists));
                                mUsername.requestFocus();
                            } catch (Exception e) {
                                Log.e("createAccount Error:", e.getMessage());
                            }
                            Toast.makeText(AddNewUserActivity.this, "Failed to add.", Toast.LENGTH_LONG).show();
                        }
                        hideProgressDialog();
                    }
                });

    }

    public void getConsoleNodesFromFirebase() {
        dbRef = Utils.getDatabase(true).getReference("Modules");
        dbRef.keepSynced(true);

        valueEventListener = dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String consoleNodes = childSnapshot.getKey();
                    mConsoleNodeValues.add(consoleNodes);
                }

                populateConsoleNodeSpinner();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void populateConsoleNodeSpinner() {
        CustomAdapter mConsoleSpinnerAdapter = new CustomAdapter(AddNewUserActivity.this, mConsoleNodeValues);
        mConsoleNode.setAdapter(mConsoleSpinnerAdapter);
    }
}

