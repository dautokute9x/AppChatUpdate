package com.example.chatappdemo.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatappdemo.R;
import com.example.chatappdemo.adapter.RecyclerViewAdapterGroup;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {
    private View groupFragmentView;
    private RecyclerView recyclerGroup;
    private RecyclerViewAdapterGroup adapterGroup;
    private static final int NUM_COLUMNS = 2;
//    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> listGroup = new ArrayList<>();
//    private DatabaseReference groupRef;
    private DatabaseReference databaseReference;
    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        groupFragmentView = inflater.inflate(R.layout.fragment_groups, container, false);
//        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        AnhXa();
//        Retrieve_DisplayGroups();
        return groupFragmentView;
    }

//    private void Retrieve_DisplayGroups() {
//        groupRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Set<String> set = new HashSet<>();
//                Iterator iterator = dataSnapshot.getChildren().iterator();
//                while (iterator.hasNext()) {
//                    set.add(((DataSnapshot)iterator.next()).getKey());
//                }
//                listGroup.clear();
//                listGroup.addAll(set);
//                adapterGroup .notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void AnhXa() {
        recyclerGroup = groupFragmentView.findViewById(R.id.recyclerGroup);
        adapterGroup = new RecyclerViewAdapterGroup(listGroup, getActivity(), new RecyclerViewAdapterGroup.AdapterListener() {
            @Override
            public void OnClick() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialog);
                builder.setTitle("Enter Group Name");
                final EditText groupNameField = new EditText(getActivity());
                groupNameField.setHint("Name group");
                builder.setView(groupNameField);
                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String groupName = groupNameField.getText().toString();
                        if (TextUtils.isEmpty(groupName)) {
                            Toast.makeText(getActivity(), "Write name group",Toast.LENGTH_SHORT).show();
                        } else {
                            CreateNewGroup(groupName);
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
        StaggeredGridLayoutManager staggeredGridLayoutManager = new
                StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);
        recyclerGroup.setLayoutManager(staggeredGridLayoutManager);
        recyclerGroup.setAdapter(adapterGroup);
    }

    private void CreateNewGroup(String groupName) {
        databaseReference.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(),"group is create successefully",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
