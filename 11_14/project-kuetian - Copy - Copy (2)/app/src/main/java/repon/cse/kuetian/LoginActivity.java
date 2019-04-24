package repon.cse.kuetian;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;




public class LoginActivity extends AppCompatActivity {
    private String email;
    private static final int REQUEST_SIGNUP = 0;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    String password;
    ProgressDialog progressDialog;

    EditText _rollNumber;
    EditText _passwordText;
    Button _loginButton;
    TextView _signupLink;
    Button forgotButton;
    Button verifyButton;
    Button loginAboutButton;

    private ScrollView linearLayout;
    private AnimationDrawable animationDrawable;

    User userCurrent = new User();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_login);

        _rollNumber = findViewById(R.id.input_email);
        _passwordText = findViewById(R.id.input_password);
        _loginButton = findViewById(R.id.btn_login);
        _signupLink = findViewById(R.id.link_signup);
        forgotButton = findViewById(R.id.forgotButton);
        verifyButton = findViewById(R.id.verifyButton);
        loginAboutButton = findViewById(R.id.loginAboutButton);

        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetUserPassword();
            }
        });
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationEmail();
            }
        });
        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm!=null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                login();
            }
        });
        loginAboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { showAbout(); }
        });
        _signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }


    public void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        }
            _loginButton.setEnabled(false);

            progressDialog = new ProgressDialog(LoginActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();

            checkRoll();
    }
    public void showAbout() {
        final Dialog aboutDialog = new Dialog(LoginActivity.this);
        aboutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        aboutDialog.setContentView(R.layout.dialog_about);
        aboutDialog.getWindow().getDecorView().getBackground().setColorFilter(0x005129a9, PorterDuff.Mode.SRC);
        aboutDialog.show();
    }
    private void checkRoll(){
        String pathReference = st.rollNumber.substring(2,4)+"/"+st.rollNumber.substring(0,4)+"/user/"+st.rollNumber;
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(pathReference);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    User newUser = dataSnapshot.getValue(User.class);
                    st.writeRollNumber(st.rollNumber,getApplicationContext());
                    Log.d(st.TAG, "rollWrite success"+st.readRollNumber(getApplicationContext()));
                    st.writeUserData(newUser,getApplicationContext());
                    Log.d(st.TAG, "writeUser success"+st.readUserData(getApplicationContext()).un);
                    email = newUser.ml;
                    _rollNumber.setError(null);
                    loginUser();
                }
                else
                {
                    st.makeText(LoginActivity.this, "Sorry you have no accout yet. Please Singup first.", Toast.LENGTH_SHORT);
                    _rollNumber.setError("Sorry you have no accout yet. Please Singup first.");
                    onLoginFailed();
                    progressDialog.dismiss();

                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                onLoginFailed();
                progressDialog.dismiss();

            }
        });
    }

    private void loginUser() {
        Log.d(st.TAG, "entered to loginUser");

        Log.d(st.TAG, email+password);

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(st.TAG, "signInWithEmail:success");
                            checkIfEmailVerified();
                            progressDialog.dismiss();
                        } else {
                            Log.d(st.TAG, "signInWithEmail:failure");
                            _passwordText.setError("LOGIN failed. Please check your Password");
                            onLoginFailed();
                            forgotButton.setVisibility(View.VISIBLE);
                            progressDialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(st.TAG, "signInWithEmail:Exception: "+e);
                progressDialog.dismiss();

            }
        });
    }
    private void checkIfEmailVerified()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified())
        {
            // user is verified, so you can finish this activity or send user to activity which you want.
            onLoginSuccess();
            st.makeText(LoginActivity.this, "Successfully logged in", Toast.LENGTH_SHORT);
        }
        else
        {
            // email is not verified, so just prompt the message to the user and restart this activity.
            // NOTE: don't forget to log out the user.
            st.makeText(this, "Email is not verified! Please verify first.", Toast.LENGTH_SHORT);
            verifyButton.setVisibility(View.VISIBLE);
            onLoginFailed();
            //FirebaseAuth.getInstance().signOut();

            //restart this activity

        }
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
                            st.makeText(LoginActivity.this, "Verification link has sent to your email. Please verify.", Toast.LENGTH_LONG);
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                            finish();
                        }
                        else
                        {
                            // email not sent, so display message and restart the activity or do whatever you wish to do
                            st.makeText(LoginActivity.this, "Verification link sent failed.", Toast.LENGTH_SHORT);

                            //restart this activity
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());

                        }
                    }
                });
    }
      @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        setResult(RESULT_OK,null);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

    }

    public void onLoginFailed() {
        st.makeText(getBaseContext(), "LOGIN failed", Toast.LENGTH_SHORT);
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        final boolean[] valid = {true};

        st.rollNumber = _rollNumber.getText().toString();
        password = _passwordText.getText().toString();

        if (st.rollNumber.isEmpty() || st.rollNumber.length()!=7) {
            _rollNumber.setError("Enter Valid Roll Number");
            valid[0] = false;
        } else {
            _rollNumber.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 15) {
            _passwordText.setError("between 6 and 15 alphanumeric characters");
            valid[0] = false;
        } else {
            _passwordText.setError(null);
        }

        return valid[0];
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
    public void resetUserPassword(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,R.style.AppTheme_Dark_Dialog);
        progressDialog.setMessage("verifying..");
        progressDialog.show();

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            st.makeText(getApplicationContext(), "Reset password instructions has sent to your email",
                                    Toast.LENGTH_SHORT);
                        }else{
                            progressDialog.dismiss();
                            st.makeText(getApplicationContext(),
                                    "Email don't exist", Toast.LENGTH_SHORT);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                st.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT);
            }
        });
    }

}
