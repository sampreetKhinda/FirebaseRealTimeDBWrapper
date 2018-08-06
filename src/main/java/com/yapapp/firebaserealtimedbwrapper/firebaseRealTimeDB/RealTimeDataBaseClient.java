package com.yapapp.firebaserealtimedbwrapper.firebaseRealTimeDB;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.yapapp.firebaserealtimedbwrapper.R;
import com.yapapp.firebaserealtimedbwrapper.model.ErrorModel;

import java.net.HttpURLConnection;
import java.util.Map;


/**
 * Created by anuj on 27/3/18.
 */
//TODO add identifier in on dataset change so that response will tell which query's data is it. (Check nouns) basicly used in web api: Done
//TODO check how to remove google json: Done
// Todo add activty : done
// Todo add custom logs : Done
// implement fluent interface : Done
// remove data work in saparate class : Done
// separate realtime database class : Done
// seprate param class : Done
// implement DI for firebase Instance
public class RealTimeDataBaseClient extends RealTimeDataBaseWrapper {
    private int limitToLast;
    private int endAt;
    private int equalTo;
    private int startAt;
    private int limitToFirst;
    private final String TAG = RealTimeDataBaseClient.class.getName();
    private Activity activity;
    private Params params;
    private FirebaseDatabase firebaseDatabase;
    private DataChangeListner dataChangeListener;
    private FirebaseAuth mAuth;

    public RealTimeDataBaseClient(Builder builder) {
        this.limitToFirst = builder.limitToFirst;
        this.limitToLast = builder.limitToLast;
        this.endAt = builder.endAt;
        this.equalTo = builder.equalTo;
        this.startAt = builder.startAt;
        this.activity = builder.activity;
        this.params = builder.params;
        //if (TextUtils.isEmpty(params.getBaseUrl())) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        //  firebaseDatabase.setPersistenceEnabled(true);
        /*} else {
            // firebaseDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(params.getBaseUrl());
        }*/
        this.dataChangeListener = builder.dataChangeListener;
    }

    @Override
    public void runQuery() {
        if (params.getQuery() != null) {
            run();
        } else {
            if (dataChangeListener != null) {
                ErrorModel firebaseError = new ErrorModel(HttpURLConnection.HTTP_BAD_METHOD, activity.getString(R.string.error_query_path_null));
                dataChangeListener.onError(firebaseError, firebaseError.getMessage(), params.getTaskId(), firebaseError.getStatusCode());
            }
        }
    }

    private void run() {
        switch (params.getQuery()) {
            case GET_ONCE:
                if (!TextUtils.isEmpty(params.getPath())) {
                    getDataOnce();
                } else {
                    ErrorModel firebaseError = new ErrorModel(HttpURLConnection.HTTP_BAD_METHOD, activity.getString(R.string.error_query_path_null));
                    dataChangeListener.onError(firebaseError, firebaseError.getMessage(), params.getTaskId(), firebaseError.getStatusCode());
                }
                break;
            case GET_CONTINUOUS:
                if (!TextUtils.isEmpty(params.getPath())) {
                    getDataContinuous();
                } else {
                    ErrorModel firebaseError = new ErrorModel(HttpURLConnection.HTTP_BAD_METHOD, activity.getString(R.string.error_query_path_null));
                    dataChangeListener.onError(firebaseError, firebaseError.getMessage(), params.getTaskId(), firebaseError.getStatusCode());
                }
                break;
            case PUT:
                if (!TextUtils.isEmpty(params.getPath())) {
                    putData();
                } else {
                    ErrorModel firebaseError = new ErrorModel(HttpURLConnection.HTTP_BAD_METHOD, activity.getString(R.string.error_query_path_null));
                    dataChangeListener.onError(firebaseError, firebaseError.getMessage(), params.getTaskId(), firebaseError.getStatusCode());
                }

                break;
            case POST:
                if (!TextUtils.isEmpty(params.getPath())) {
                    postData();
                } else {
                    ErrorModel firebaseError = new ErrorModel(HttpURLConnection.HTTP_BAD_METHOD, activity.getString(R.string.error_query_path_null));
                    dataChangeListener.onError(firebaseError, firebaseError.getMessage(), params.getTaskId(), firebaseError.getStatusCode());
                }

                break;
            case DELETE:
                if (!TextUtils.isEmpty(params.getPath())) {
                    deleteData();
                } else {
                    ErrorModel firebaseError = new ErrorModel(HttpURLConnection.HTTP_BAD_METHOD, activity.getString(R.string.error_query_path_null));
                    dataChangeListener.onError(firebaseError, firebaseError.getMessage(), params.getTaskId(), firebaseError.getStatusCode());
                }

                break;
            case AUTHENTICATE:
                authenticate();
                break;
            case LOGOUT:
                logOut();
                break;
            case SIGNUP:
                signup();
                break;
        }
    }

    private void signup() {
        mAuth = FirebaseAuth.getInstance();

        Log.d(TAG, "signUp");
        if (!validateForm()) {
            return;
        }
        mAuth.createUserWithEmailAndPassword(params.getEmail(), params.getPassword())
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser(), params.getDisplayName());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (dataChangeListener != null) {
                    ErrorModel firebaseError = new ErrorModel(HttpURLConnection.HTTP_BAD_METHOD, e.getMessage());
                    dataChangeListener.onError(firebaseError, e.getMessage(), params.getTaskId(), firebaseError.getStatusCode());
                }
            }
        });
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        dataChangeListener.onDataChange(null, params.getTaskId());
    }

    private void authenticate() {
        mAuth = FirebaseAuth.getInstance();

        Log.d(TAG, "signIn");
        if (!validateForm()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(params.getEmail(), params.getPassword()).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                if (task.isSuccessful()) {
                    onAuthSuccess(task.getResult().getUser());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (dataChangeListener != null) {
                    ErrorModel firebaseError = new ErrorModel(HttpURLConnection.HTTP_BAD_METHOD, e.getMessage());
                    dataChangeListener.onError(firebaseError, e.getMessage(), params.getTaskId(), firebaseError.getStatusCode());
                }
            }
        });
    }

    private void onAuthSuccess(FirebaseUser user) {
        if (dataChangeListener != null) {
            dataChangeListener.onDataChange(user, params.getTaskId());
        }

    }

    private void onAuthSuccess(FirebaseUser user, String username) {
        if (dataChangeListener != null) {
            dataChangeListener.onDataChange(user, params.getTaskId());
        }
        // Write new user
        writeNewUser(user, username);

        // Go to MainActivity

    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(params.getEmail())) {
            ErrorModel errorModel = new ErrorModel(HttpURLConnection.HTTP_BAD_REQUEST, activity.getString(R.string.email_err));
            dataChangeListener.onError(errorModel, errorModel.getMessage(), params.getTaskId(), errorModel.getStatusCode());
            result = false;
        } else {

        }

        if (TextUtils.isEmpty(params.getPassword())) {
            ErrorModel errorModel = new ErrorModel(HttpURLConnection.HTTP_BAD_REQUEST, activity.getString(R.string.password_err));
            dataChangeListener.onError(errorModel, errorModel.getMessage(), params.getTaskId(), errorModel.getStatusCode());
            result = false;
        } else {
        }

        return result;
    }

    // [START basic_write]
    private void writeNewUser(FirebaseUser user, String name) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
        user.updateProfile(profileUpdates);
    }

   /* private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }*/

    /**
     * delete from  database
     */
    @Override
    public void deleteData() {
        DatabaseReference mDatabase = firebaseDatabase.getReference(params.getPath());
        mDatabase.removeValue();
        mDatabase.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                dataChangeListener.onDataChange(databaseError, params.getTaskId());
            }
        });
    }

    /**
     * Post data in database
     */
    @Override
    public void postData() {
        if (params.getData() != null) {
            if (!TextUtils.isEmpty(params.getValue())) {
                firebaseDatabase.getReference(params.getPath()).child(params.getValue()).setValue(params.getData()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dataChangeListener.onDataChange(task, params.getTaskId());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ErrorModel firebaseError = new ErrorModel(HttpURLConnection.HTTP_BAD_REQUEST, e.getMessage());
                        dataChangeListener.onError(firebaseError, e.getMessage(), params.getTaskId(), firebaseError.getStatusCode());
                    }
                });
            } else {
                String userId = firebaseDatabase.getReference(params.getPath()).push().getKey();
                firebaseDatabase.getReference(params.getPath()).child(userId).setValue(params.getData()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dataChangeListener.onDataChange(task, params.getTaskId());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ErrorModel firebaseError = new ErrorModel(HttpURLConnection.HTTP_BAD_REQUEST, e.getMessage());
                        dataChangeListener.onError(firebaseError, e.getMessage(), params.getTaskId(), firebaseError.getStatusCode());
                    }
                });
            }
        } else {
            ErrorModel firebaseError = new ErrorModel(HttpURLConnection.HTTP_BAD_REQUEST, activity.getString(R.string.empity_value_error));
            dataChangeListener.onError(firebaseError, firebaseError.getMessage(), params.getTaskId(), firebaseError.getStatusCode());
        }
    }

    /**
     * Put Data in Database
     */
    @Override
    public void putData() {
        if (params.getData() != null) {
            Map<String, Object> paramsMap = convertPojoToMap(params.getData());
            firebaseDatabase.getReference(params.getPath()).child(params.getValue()).updateChildren(paramsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    dataChangeListener.onDataChange(task, params.getTaskId());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ErrorModel firebaseError = new ErrorModel(HttpURLConnection.HTTP_BAD_REQUEST, e.getMessage());
                    dataChangeListener.onError(firebaseError, e.getMessage(), params.getTaskId(), firebaseError.getStatusCode());
                }
            });
        } else {
            ErrorModel firebaseError = new ErrorModel(HttpURLConnection.HTTP_BAD_REQUEST, activity.getString(R.string.empity_value_error));
            dataChangeListener.onError(firebaseError, firebaseError.getMessage(), params.getTaskId(), firebaseError.getStatusCode());
        }
    }

    /**
     * Get values from database
     */
    @Override
    public void getDataOnce() {
        if (!TextUtils.isEmpty(params.getValue())) {
            firebaseDatabase.getReference(params.getPath()).child(params.getValue()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (params.getClassName() != null) {
                        dataChangeListener.onDataChange(dataSnapshot.getValue(params.getClassName()), params.getTaskId());
                    } else {
                        dataChangeListener.onDataChange(dataSnapshot, params.getTaskId());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "Failed to read params.getValue().", databaseError.toException());
                    ErrorModel firebaseError = new ErrorModel(HttpURLConnection.HTTP_BAD_REQUEST, databaseError.toException().getMessage());
                    dataChangeListener.onError(firebaseError, databaseError.getMessage(), params.getTaskId(), databaseError.getCode());

                }
            });
        } else {
            firebaseDatabase.getReference(params.getPath()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (params.getClassName() != null) {
                        dataChangeListener.onDataChange(dataSnapshot.getValue(params.getClassName()), params.getTaskId());
                    } else {
                        dataChangeListener.onDataChange(dataSnapshot, params.getTaskId());
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w(TAG, "Failed to read params.getValue().", error.toException());
                    ErrorModel firebaseError = new ErrorModel(HttpURLConnection.HTTP_BAD_REQUEST, error.toException().getMessage());
                    dataChangeListener.onError(firebaseError, error.toException().getMessage(), params.getTaskId(), error.getCode());
                }
            });
        }
    }

    @Override
    public void getDataContinuous() {
        if (!TextUtils.isEmpty(params.getValue())) {
            firebaseDatabase.getReference(params.getPath()).child(params.getValue()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dataChangeListener.onDataChange(dataSnapshot, params.getTaskId());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "Failed to read value.", databaseError.toException());
                    ErrorModel firebaseError = new ErrorModel(HttpURLConnection.HTTP_BAD_REQUEST, databaseError.toException().getMessage());
                    dataChangeListener.onError(firebaseError, databaseError.toException().getMessage(), params.getTaskId(), databaseError.getCode());
                }
            });
        } else {
            firebaseDatabase.getReference(params.getPath()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dataChangeListener.onDataChange(dataSnapshot, params.getTaskId());
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w(TAG, "Failed to read value.", error.toException());
                    ErrorModel firebaseError = new ErrorModel(HttpURLConnection.HTTP_BAD_REQUEST, error.toException().getMessage());
                    dataChangeListener.onError(firebaseError, error.toException().getMessage(), params.getTaskId(), error.getCode());
                }
            });
        }
    }

    /**
     * Functionality to convert pojo into map
     */
    <T> Map convertPojoToMap(T pojo) {
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(pojo);
        return gson.fromJson(jsonElement, Map.class);
    }
}