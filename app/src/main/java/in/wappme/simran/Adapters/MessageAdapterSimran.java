package in.wappme.simran.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import in.wappme.simran.Models.MessageModel;
import in.wappme.simran.R;


public class MessageAdapterSimran  extends RecyclerView.Adapter{
    Context context;
    ArrayList<MessageModel> arrayList;

    public MessageAdapterSimran(Context context, ArrayList<MessageModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    public MessageAdapterSimran() {
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case 0:
                view = LayoutInflater.from(context).inflate(R.layout.user_msg,parent,false);
                return new UserMsgViewHolder(view);
            case 1:
                view = LayoutInflater.from(context).inflate(R.layout.bot_msg,parent,false);
                return new BotMsgViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel model = arrayList.get(position);
        switch (model.getSender()){
            case "user":
                ((UserMsgViewHolder) holder).userMsgTextView.setText(model.getMessage());
                break;
            case "bot":
                ((BotMsgViewHolder)holder).botMsgTextView.setText(model.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (arrayList.get(position).getSender()){
            case "user":
                return 0;
            case "bot":
                return 1;
            default:
                return -1;
        }
    }

    public static class UserMsgViewHolder extends RecyclerView.ViewHolder{
            TextView userMsgTextView;
        public UserMsgViewHolder(@NonNull View itemView) {
            super(itemView);
            userMsgTextView = itemView.findViewById(R.id.userMsgTextView);
        }
    }

    public static class BotMsgViewHolder extends RecyclerView.ViewHolder{
            TextView botMsgTextView;
        public BotMsgViewHolder(@NonNull View itemView) {
            super(itemView);
            botMsgTextView = itemView.findViewById(R.id.botMsgTextView);
        }
    }
}
