package com.example.chatappdemo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
public class ContactsFragment extends Fragment {
    private View contactsView;
    private RecyclerView recyclerViewContacts;
    private DatabaseReference contactsRef, userRef;
    private FirebaseAuth firebaseAuth;
    private String currentUserId;

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contactsView = inflater.inflate(R.layout.fragment_contacts, container, false);
        recyclerViewContacts = contactsView.findViewById(R.id.recycler_contact_list);
        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        return contactsView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(contactsRef, User.class)
                .build();

        FirebaseRecyclerAdapter<User, ContactsViewHolder> adapter =
                new FirebaseRecyclerAdapter<User, ContactsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ContactsViewHolder contactsViewHolder, int i, @NonNull User user) {
                        String userIds = getRef(i).getKey();
                        userRef.child(userIds).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild("image")) {
                                    String userImage = dataSnapshot.child("image").getValue().toString();
                                    String userName = dataSnapshot.child("name_signup").getValue().toString();
                                    String userSex = dataSnapshot.child("gioitinh_signup").getValue().toString();

                                    contactsViewHolder.tv_username.setText(userName);
                                    contactsViewHolder.tv_gioi_tinh.setText(userSex);
                                    Picasso.with(getActivity()).load(userImage).placeholder(R.drawable.user_profile).into(contactsViewHolder.profileImage);
                                } else {
                                    String userName = dataSnapshot.child("name_signup").getValue().toString();
                                    String userSex = dataSnapshot.child("gioitinh_signup").getValue().toString();

                                    contactsViewHolder.tv_username.setText(userName);
                                    contactsViewHolder.tv_gioi_tinh.setText(userSex);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
                        ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                        return viewHolder;
                    }
                };
        recyclerViewContacts.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder {
        TextView tv_username, tv_gioi_tinh;
        CircleImageView profileImage;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_username = itemView.findViewById(R.id.tv_user_name);
            profileImage = (CircleImageView) itemView.findViewById(R.id.user_profile);
            tv_gioi_tinh = itemView.findViewById(R.id.tv_gioi_tinh);
        }
    }
}
