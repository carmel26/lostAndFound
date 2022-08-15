package com.example.lostfound.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lostfound.Activities.MainActivity;
import com.example.lostfound.Activities.ProfileActivity;
import com.example.lostfound.Classes.Message;
import com.example.lostfound.Classes.User;
import com.example.lostfound.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MessageAdapter extends ArrayAdapter<Message>  {

    // Declare variable

    private AppCompatActivity context;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private List<Message> messageList;

    private TextView textViewUser, textViewMessage;

    // Pass in message list
    public MessageAdapter(AppCompatActivity context, List<Message> messageList){
        super(context, R.layout.card_message,messageList);
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View messageView = inflater.inflate(R.layout.card_message, null, true);

        // Initialize
        textViewUser = (TextView) messageView.findViewById(R.id.textViewUser);
        textViewMessage = (TextView) messageView.findViewById(R.id.textViewMessage);

        textViewMessage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        final Message message = messageList.get(position);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + firebaseAuth.getCurrentUser().getUid()  + "/INFO/");
//        System.out.println(databaseReference+" ooo");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                // Set message to textview
                if (user.getName().equalsIgnoreCase(message.getUser()) ){
                    textViewUser.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                }else{
                    textViewUser.setTextColor(ContextCompat.getColor(context, R.color.blackText));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        textViewUser.setText(message.getUser());
        textViewMessage.setText(message.getText());
        return messageView;
    }
}
