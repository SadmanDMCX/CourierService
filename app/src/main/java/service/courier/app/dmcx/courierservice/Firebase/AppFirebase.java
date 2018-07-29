package service.courier.app.dmcx.courierservice.Firebase;

import android.net.Uri;
import android.support.annotation.NonNull;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppFirebase {

    private final String EXCEPTION_MESSAGE = "Exception Found!";

    // Interface
    public interface FirebaseCallback {
        void ProcessCallback(boolean isTaskCompleted);
        void ExceptionCallback(String exception);
    }

    private FirebaseAuth mAuth;
    private DatabaseReference mReference;
    private DatabaseReference mAdminReference;
    private DatabaseReference mEmployeeReference;
    private DatabaseReference mWorksReference;
    private DatabaseReference mStatusReference;
    private DatabaseReference mNotificationsReference;

    private StorageReference mStorageReference;

    public AppFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference();
        mAdminReference = FirebaseDatabase.getInstance().getReference().child(AFModel.users).child(AFModel.admins);
        mEmployeeReference = FirebaseDatabase.getInstance().getReference().child(AFModel.users).child(AFModel.employees);
        mWorksReference = FirebaseDatabase.getInstance().getReference().child(AFModel.users).child(AFModel.works);
        mStatusReference = FirebaseDatabase.getInstance().getReference().child(AFModel.users).child(AFModel.status);
        mNotificationsReference = FirebaseDatabase.getInstance().getReference().child(AFModel.users).child(AFModel.notifications);

        mStorageReference = FirebaseStorage.getInstance().getReference();
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

    public FirebaseAuth getFirebaseAuth() {
        return mAuth;
    }

    public String getCurrentUserId() {
        return mAuth.getCurrentUser().getUid();
    }

    public DatabaseReference getDbReference() {
        return mReference;
    }

    public DatabaseReference getDbAdminsReference() {
        return mAdminReference;
    }

    public DatabaseReference getDbWorksReference() {
        return mWorksReference;
    }

    public DatabaseReference getDbEmployeesReference() {
        return mEmployeeReference;
    }

    public DatabaseReference getDbStatusReference() {
        return mStatusReference;
    }

    public DatabaseReference getDbNotificationsReference() {
        return mNotificationsReference;
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

    public void check(DatabaseReference reference, final FirebaseCallback callback) {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                callback.ProcessCallback(dataSnapshot.getValue() != null);
                callback.ExceptionCallback("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void checkEmployee(final FirebaseCallback callback) {
        mEmployeeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getCurrentUser() != null) {
                    if (dataSnapshot.hasChild(getCurrentUserId())) {
                        callback.ProcessCallback(true);
                    } else {
                        callback.ProcessCallback(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.ExceptionCallback(databaseError.getMessage());
            }
        });
    }

    public void checkEmployeeData(DataSnapshot snapshot, final FirebaseCallback callback) {
        List<String> params = new ArrayList<>();
        params.add(AFModel.id);
        params.add(AFModel.phone_no);
        params.add(AFModel.admin_id);
        params.add(AFModel.email);
        params.add(AFModel.password);

        for (String item : params) {
            if (!snapshot.hasChild(item)) {
                callback.ProcessCallback(false);
                return;
            }
        }

        callback.ProcessCallback(true);
    }

    public void isUserAdmin(final FirebaseCallback callback) {
        final String uid = mAuth.getCurrentUser().getUid();
        mAdminReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(uid)) {
                    callback.ProcessCallback(true);
                    callback.ExceptionCallback("");
                } else {
                    callback.ProcessCallback(false);
                    callback.ExceptionCallback("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public void addUserImageToStorage(final DatabaseReference reference, final Map<String, Object> map, Uri imageUri, final FirebaseCallback callback) {
        mStorageReference.child(getCurrentUserId() + ".jpg").putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            callback.ProcessCallback(true);
                        }
                    });
                }
                else
                    callback.ProcessCallback(false);

                callback.ExceptionCallback(task.getException().toString());
            }
        });
    }
}
