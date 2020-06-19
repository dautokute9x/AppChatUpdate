package com.example.chatappdemo.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chatappdemo.R;
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
public class ChatsFragment extends Fragment {
    private View ChatsView;
    private RecyclerView recyclerViewChats;
    private DatabaseReference chatsReference, usersReference;
    private FirebaseAuth firebaseAuth;
    private String currentUserId, retImage, retName, retStatus;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ChatsView = inflater.inflate(R.layout.fragment_chats, container, false);
//        firebaseAuth = FirebaseAuth.getInstance();
//        currentUserId = firebaseAuth.getCurrentUser().getUid();
//        chatsReference = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
//        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
//
//        recyclerViewChats = ChatsView.findViewById(R.id.chats_list);
//        recyclerViewChats.setLayoutManager(new LinearLayoutManager(getContext()));
        return ChatsView;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        FirebaseRecyclerOptions<User> options =
//                new FirebaseRecyclerOptions.Builder<User>()
//                .setQuery(chatsReference, User.class)
//                .build();
//
//        FirebaseRecyclerAdapter<User, ChatsViewHolder> adapter =
//                new FirebaseRecyclerAdapter<User, ChatsViewHolder>(options) {
//                    @Override
//                    protected void onBindViewHolder(@NonNull final ChatsViewHolder chatsViewHolder, int i, @NonNull User user) {
//                        final String usersIds = getRef(i).getKey();
//                        usersReference.child(usersIds).addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                if (dataSnapshot.exists()) {
//                                    if (dataSnapshot.hasChild("imgAnhDD")) {
//                                        retImage = dataSnapshot.child("imgAnhDD").getValue().toString();
//                                        Picasso.with(getActivity()).load(retImage).placeholder(R.drawable.user_profile).into(chatsViewHolder.profileImage_chat);
//                                    }
//                                    retName = dataSnapshot.child("name").getValue().toString();
//                                    retStatus = dataSnapshot.child("status").getValue().toString();
//                                    chatsViewHolder.userName_chat.setText(retName);
//                                    chatsViewHolder.user_status_chat.setText("New Mess");
//                                    chatsViewHolder.user_lastTime_chat.setText("Time");
//
//                                    chatsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
//                                            chatIntent.putExtra("visit_user_id", usersIds);
//                                            chatIntent.putExtra("visit_user_image", retImage);
//                                            chatIntent.putExtra("visit_user_name", retName);
//                                            startActivity(chatIntent);
//                                        }
//                                    });
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
//
//                    }
//
//                    @NonNull
//                    @Override
//                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item_chats, parent, false);
//                        return new  ChatsViewHolder(view);
//                    }
//                };
//        recyclerViewChats.setAdapter(adapter);
//        adapter.startListening();
//    }
//
//    public static class ChatsViewHolder extends RecyclerView.ViewHolder {
//        CircleImageView profileImage_chat, on_off_chat;
//        TextView userName_chat, user_status_chat, user_lastTime_chat;
//
//        public ChatsViewHolder(@NonNull View itemView) {
//            super(itemView);
//            profileImage_chat = itemView.findViewById(R.id.user_profile_chat);
//            on_off_chat = itemView.findViewById(R.id.user_on_off_chat);
//            userName_chat = itemView.findViewById(R.id.tv_username_chat);
//            user_status_chat = itemView.findViewById(R.id.tv_status_chat);
//            user_lastTime_chat = itemView.findViewById(R.id.tv_time_chat);
//        }
//    }
}
