package repon.cse.kuetian;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    ProgressDialog progressDialog;

    EditText _nameText;
    EditText _emailText;
    EditText _rollNumber;
    EditText _passwordText;
    EditText _reEnterPasswordText;
    Button _signupButton;
    Button _loginLink;

    String name;
    String email;
    String password;
    String reEnterPassword;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_signup);

         _nameText = findViewById(R.id.input_name);
        _emailText = findViewById(R.id.input_email);
        _emailText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus&&_emailText.getText().toString().isEmpty())
                    _emailText.setText(_rollNumber.getText().toString()+"@stud.kuet.ac.bd");
            }
        });

         _rollNumber = findViewById(R.id.input_rollNumber);
         _passwordText = findViewById(R.id.input_password);
         _reEnterPasswordText = findViewById(R.id.input_reEnterPassword);
         _signupButton = findViewById(R.id.btn_signup);
         _loginLink = findViewById(R.id.link_login);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm!=null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            }
        });

    }

    public void signup() {
        Log.d(st.TAG, "signup");

        if (!validate())
        {
            onSignupFailed();
            return;
        }


        _signupButton.setEnabled(false);
        progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        // TODO: Implement your own signup logic here.

        checkROll();

    }

    private void checkROll() {
        String pathReference = st.rollNumber.substring(2,4)+"/"+st.rollNumber.substring(0,4)+"/"+st.rollNumber;
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(pathReference);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    _rollNumber.setError("Sorry you have already created account. Please Login");
                    onSignupFailed();
                    progressDialog.dismiss();
                    return;
                }
                else
                {
                    _rollNumber.setError(null);
                    createUser();
                    return;
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                _rollNumber.setError("Failed to check Roll number");
                onSignupFailed();
                progressDialog.dismiss();
                return;
            }
        });
    }


    private void createUser() {

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(st.TAG,"SIGNUP successful!");
                            st.makeText(SignupActivity.this, "signUp:success", Toast.LENGTH_SHORT);

                            User newUser = new User(name,email);
                            newUser.dp = st.initDeptList().get(st.rollNumber.substring(2,4));

                            st.writeRollNumber(st.rollNumber,getApplicationContext());
                            st.writeUserData(newUser,getApplicationContext());
                            String pathReference = st.rollNumber.substring(2,4)+"/"+st.rollNumber.substring(0,4)+"/user/"+st.rollNumber;
                            FirebaseDatabase.getInstance().getReference(pathReference).setValue(newUser);
                            FirebaseDatabase.getInstance().getReference(mAuth.getCurrentUser().getUid()).setValue(st.rollNumber);
                            progressDialog.dismiss();
                            sendVerificationEmail();
                        } else {
                            // If sign in fails, display a message to the user.
                            _emailText.setError("Sorry this email has already used!");
                            st.makeText(SignupActivity.this, "Sorry this email has already used!", Toast.LENGTH_SHORT);
                            onSignupFailed();
                            progressDialog.dismiss();
                        }
                    }
                });
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }
    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        //moveTaskToBack(true);
    }

    public void onSignupFailed() {
        st.makeText(getBaseContext(), "Sign Up failed!", Toast.LENGTH_SHORT);
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        final boolean[] valid = {true};

        name = _nameText.getText().toString();
        email = _emailText.getText().toString();
        st.rollNumber = _rollNumber.getText().toString();
        password = _passwordText.getText().toString();
        reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("At least 3 characters");
            valid[0] = false;
        } else {
            _nameText.setError(null);
        }
        if (st.rollNumber.isEmpty() || st.rollNumber.length()!=7) {
            _rollNumber.setError("Enter Valid Roll Number");
            valid[0] = false;
        } else {
            _rollNumber.setError(null);
        }
        if (email.isEmpty() || !email.contains("@stud.kuet.ac.bd")||!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Enter your stud.kuet.ac.bd email address");
            valid[0] = false;
        } else {
            _emailText.setError(null);
        }
        if (password.isEmpty() || password.length() < 6 || password.length() > 15) {
            _passwordText.setError("Between 6 and 15 alphanumeric characters");
            valid[0] = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 6 || reEnterPassword.length() > 15 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid[0] = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid[0];
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
    private void sendVerificationEmail()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email sent
                            // after email is sent just logout the user and finish this activity
                            st.makeText(SignupActivity.this, "Verification link has sent to your email. Please verify.", Toast.LENGTH_LONG);
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            finish();
                        }
                        else
                        {
                            // email not sent, so display message and restart the activity or do whatever you wish to do
                            st.makeText(SignupActivity.this, "Verification link sent failed.", Toast.LENGTH_SHORT);

                            //restart this activity
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());

                        }
                    }
                });
    }

}