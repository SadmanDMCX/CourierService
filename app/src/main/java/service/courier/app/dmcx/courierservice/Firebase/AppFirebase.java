package service.courier.app.dmcx.courierservice.Firebase;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;
import java.util.Objects;

public class AppFirebase {

    private final String EXCEPTION_MESSAGE = "Exception Found!";

    // Interface
    public interface FirebaseCallback {
        void ProcessCallback(boolean isTaskCompleted);
        void ExceptionCallback(String exception);
    }

    private FirebaseAuth mAuth;
    private DatabaseReference mReference;

    public AppFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference();
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public void signUpUser(String email, String passwd, final FirebaseCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, passwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                callback.ProcessCallback(task.isSuccessful());

                if (task.getException() != null) {
                    callback.ExceptionCallback(task.getException().getMessage());
                }
            }
        });
    }

    public void signInUser(String email, String passwd, final FirebaseCallback callback) {
        mAuth.signInWithEmailAndPassword(email, passwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                callback.ProcessCallback(task.isSuccessful());

                if (task.getException() != null) {
                    callback.ExceptionCallback(task.getException().getMessage());
                }
            }
        });
    }

    public void signOutUser() {
        mAuth.signOut();
    }

    public DatabaseReference getDbReference() {
        return mReference;
    }

    public void insert(DatabaseReference reference, Map map, final FirebaseCallback callback) {
        reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                callback.ProcessCallback(task.isSuccessful());
                if (task.getException() != null) {
                    callback.ExceptionCallback(task.getException().getMessage());
                }
            }
        });
    }

}
