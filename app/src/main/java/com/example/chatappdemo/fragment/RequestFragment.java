package com.example.chatappdemo.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappdemo.R;
import com.example.chatappdemo.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {
    private View RequestFragmentView;
    private RecyclerView recyclerView;
    private DatabaseReference requestReference, userReference, contactReference;
    private FirebaseAuth firebaseAuth;
    private String currentUserID, requestUserName, requestUserStatus, requestUserAnhDD;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RequestFragmentView = inflater.inflate(R.layout.fragment_request, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();
        requestReference = FirebaseDatabase.getInstance().getReference().child("Requests");
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        contactReference = FirebaseDatabase.getInstance().getReference().child("Contacts");
        recyclerView = RequestFragmentView.findViewById(R.id.request_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return RequestFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(requestReference.child(currentUserID), User.class)
                .build();

        FirebaseRecyclerAdapter<User, RequestsViewHolder> adapter =
                new FirebaseRecyclerAdapter<User, RequestsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestsViewHolder requestsViewHolder, int i, @NonNull User user) {
                        requestsViewHolder.itemView.findViewById(R.id.request_accept_btn).setVisibility(View.VISIBLE);
                        requestsViewHolder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.VISIBLE);
                        final String list_user_id = getRef(i).getKey();
                        DatabaseReference getTypeRef = getRef(i).child("request_type").getRef();
                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String type = dataSnapshot.getValue().toString();
                                    if (type.equals("received")) {
                                        userReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.hasChild("imgAnhDD")) {
                                                    requestUserAnhDD = dataSnapshot.child("imgAnhDD").getValue().toString();
                                                    Picasso.with(getActivity()).load(requestUserAnhDD).placeholder(R.drawable.user_profile).into(requestsViewHolder.profileImage);
                                                }
                                                    requestUserName = dataSnapshot.child("name").getValue().toString();
                                                    requestUserStatus = dataSnapshot.child("status").getValue().toString();

                                                    requestsViewHolder.tv_username.setText(requestUserName);
                                                    requestsViewHolder.tv_status_item.setText("Want to connect with you!");

                                                requestsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        CharSequence options[] = new CharSequence[] {
                                                            "Accept", "Cancel"
                                                        };
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                        builder.setTitle(requestUserName + " request");
                                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                if (which == 0) {
                                                                    contactReference.child(currentUserID).child(list_user_id).child("Contact")
                                                                            .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                contactReference.child(list_user_id).child(currentUserID).child("Contact")
                                                                                        .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            requestReference.child(currentUserID).child(list_user_id)
                                                                                                    .removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful()) {
                                                                                                                requestReference.child(list_user_id).child(currentUserID)
                                                                                                                        .removeValue()
                                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                if (task.isSuccessful()) {
                                                                                                                                    Toast.makeText(getContext(),"New contact saved", Toast.LENGTH_SHORT).show();
                                                                                                                                }
                                                                                                                            }
                                                                                                                        });
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                                if (which == 1) {
                                                                    contactReference.child(currentUserID).child(list_user_id).child("Contact")
                                                                            .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                contactReference.child(list_user_id).child(currentUserID).child("Contact")
                                                                                        .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            requestReference.child(currentUserID).child(list_user_id)
                                                                                                    .removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful()) {
                                                                                                                requestReference.child(list_user_id).child(currentUserID)
                                                                                                                        .removeValue()
                                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                if (task.isSuccessful()) {
                                                                                                                                    Toast.makeText(getContext(),"Contact deleted", Toast.LENGTH_SHORT).show();
                                                                                                                                }
                                                                                                                            }
                                                                                                                        });
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });
                                                        builder.show();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
                        RequestsViewHolder holder = new RequestsViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder {
        TextView tv_username, tv_status_item;
        CircleImageView profileImage;
        CircleImageView img_On_Off;
        Button btnAccept, btnCancel;

        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_username = itemView.findViewById(R.id.tv_user_name);
            profileImage = (CircleImageView) itemView.findViewById(R.id.user_profile);
            img_On_Off = (CircleImageView) itemView.findViewById(R.id.user_on_off);
            tv_status_item = itemView.findViewById(R.id.tv_status_item);
            btnAccept = itemView.findViewById(R.id.request_accept_btn);
            btnCancel = itemView.findViewById(R.id.request_cancel_btn);
        }
    }
}
