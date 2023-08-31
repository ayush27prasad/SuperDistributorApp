package com.app.superdistributor;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.superdistributor.admin.notification.AdminNotificationActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder>{

    Context context;
    ArrayList<NotificationItemModel> list;
    DatabaseReference databaseReference;
    public NotificationAdapter(Context context, ArrayList<NotificationItemModel> list){
        this.context = context;
        this.list = list;
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }
    @NonNull
    @Override
    public NotificationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationAdapter.MyViewHolder(
                LayoutInflater.from(context).inflate(R.layout.complaint_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.MyViewHolder holder, int position) {

        NotificationItemModel notificationItemModel = list.get(position);
        holder.type.setText(notificationItemModel.getNotificationType());
        holder.tag.setText(notificationItemModel.getNotificationTag());
        holder.description.setText(notificationItemModel.getNotificationDesc());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    showExpandedDialog(holder.getAdapterPosition(),
                            notificationItemModel.getNotificationType(),
                            notificationItemModel.getNotificationTag(),
                            notificationItemModel.getNotificationDesc());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        CardView item;
        TextView type, tag, description;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.complaint_item_cv);
            type = itemView.findViewById(R.id.dealer_name);
            tag = itemView.findViewById(R.id.tag);
            description = itemView.findViewById(R.id.complaint_desc);
        }
    }

  private void showExpandedDialog(int position, String type, String tag, String description) {
      AlertDialog.Builder builder = new AlertDialog.Builder(context);
      View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_expanded_view, null);
      TextView tagTv = dialogView.findViewById(R.id.notif_tag);
      TextView descTv = dialogView.findViewById(R.id.notif_description);
      builder.setView(dialogView);
      tagTv.setText(tag);
      descTv.setText(description);
      HashMap<String,Object> updateStatus = new HashMap<>();
      builder.setPositiveButton("Reject", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
              updateStatus.put("Status","Rejected");
              if(type.equals("SR Product Confirmation")){
                  databaseReference.child("Admin").child("Notifications")
                          .child("ProductConfirmation").child("SRs")
                          .child(tag).updateChildren(updateStatus);
              }
              else if (type.equals("Dealer Complaint")) {
                  databaseReference.child("Dealers").child("RequestServices")
                          .child("RegisterComplaints").child(tag)
                          .updateChildren(updateStatus);
              }
              else if (type.equals("Replacement by Dealer")) {
                      databaseReference.child("Dealers").child("RequestServices")
                              .child("ReplacementByDealer").child(tag)
                              .updateChildren(updateStatus);
              }
              else if (type.equals("Grievance")) {
                  databaseReference.child("Grievances").child(tag).removeValue();
              }
              if(position<list.size()) list.remove(position);
              ((Activity)context).finish();
              context.startActivity(new Intent(context,AdminNotificationActivity.class));
              Toast.makeText(context, "Status updated", Toast.LENGTH_SHORT).show();
          }
      }).setNegativeButton("Accept", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
              updateStatus.put("Status","Accepted");
              if(type.equals("SR Product Confirmation")){
                  databaseReference.child("Admin").child("Notifications")
                          .child("ProductConfirmation").child("SRs")
                          .child(tag).updateChildren(updateStatus);
              }
              else if (type.equals("Dealer Complaint")) {
                  databaseReference.child("Dealers").child("RequestServices")
                          .child("RegisterComplaints").child(tag)
                          .updateChildren(updateStatus);
              }
              else if (type.equals("Replacement by Dealer")) {
                  databaseReference.child("Dealers").child("RequestServices")
                          .child("ReplacementByDealer").child(tag)
                          .updateChildren(updateStatus);
              }
              else if (type.equals("Grievance")) {
                  databaseReference.child("Grievances").child(tag).removeValue();
              }
              if(position<list.size()) list.remove(position);
              ((Activity)context).finish();
              context.startActivity(new Intent(context,AdminNotificationActivity.class));
              Toast.makeText(context, "Status updated", Toast.LENGTH_SHORT).show();
          }
      });
      AlertDialog dialog = builder.create();
      dialog.setTitle(type);
      dialog.show();
  }
}
