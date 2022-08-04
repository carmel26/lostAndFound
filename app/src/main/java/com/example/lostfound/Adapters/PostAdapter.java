package com.example.lostfound.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lostfound.Classes.Post;
import com.example.lostfound.Fragments.LostFragment;
import com.example.lostfound.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.fragment.app.FragmentActivity;


public class PostAdapter extends ArrayAdapter<Post> {

    // Declare variables

    private FragmentActivity context;
    private String  imageUrl,type;
    private DatabaseReference databaseReference;

    private List<Post> postList;

    private TextView textViewTitle, textViewDescription;
    private ImageView imagePosted;

    public PostAdapter(FragmentActivity context, List<Post> postList,String type){
        super(context, R.layout.card_post,postList);
        this.context = context;
        this.postList = postList;
        this.type = type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        final View view = inflater.inflate(R.layout.card_post, null, true);

        // Initialize
        textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
        textViewDescription = (TextView)view.findViewById(R.id.textViewDescription);


        textViewDescription.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        Post post = postList.get(position);

        textViewTitle.setText(post.getTitle());
        textViewDescription.setText(post.getDescription());

        databaseReference = FirebaseDatabase.getInstance().getReference("/" + type + "/" + post.getPostId() + "/IMAGE");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imagePosted =view.findViewById(R.id.imagePosted);
                imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);

                Picasso.get().load(imageUrl).resize(50,50).into(imagePosted);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }
}
