package com.example.metaucapstone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.metaucapstone.models.Recipe;

import java.util.List;
import java.util.Map;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    FragmentManager fragmentManager;
    Context context;
    List<Map<String, Object>> users;

    public UserAdapter(FragmentManager fragmentManager, Context context, List<Map<String, Object>> users) {
        this.fragmentManager = fragmentManager;
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View userView = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserAdapter.ViewHolder(userView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        String uid;
        TextView tvUsername;
        ImageView ivPic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvItemUser);
            ivPic = itemView.findViewById(R.id.ivItemUser);
        }

        @Override
        public void onClick(View v) {

        }

        public void bind(Map<String, Object> user) {
            uid = (String) user.get("uid");
            tvUsername.setText((String) user.get("username"));
            Glide.with(context)
                    .load(user.get("imageUrl"))
                    .circleCrop()
                    .into(ivPic);
            tvUsername.setOnClickListener(toProfile);
            ivPic.setOnClickListener(toProfile);
        }

        private View.OnClickListener toProfile = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ProfileFragment(uid);
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            }
        };
    }
}
