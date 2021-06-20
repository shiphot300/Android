package com.example.mainpage.write;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mainpage.R;

import java.util.ArrayList;
import java.util.List;

public abstract class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
    private List<String> itemList;
    private Context mContext;

    public abstract void itemCallback(int index);

    public PhotoAdapter(Context context) {
        this.mContext = context;
    }


    public void addItem(String item) {
        if (this.itemList == null) {
            this.itemList = new ArrayList<>();
            itemList.add(item);
            notifyDataSetChanged();
        } else {
            int position = this.itemList.size();
            this.itemList.add(item);
            notifyItemInserted(position);
        }
    }

    public void removeItem(int position) {
        if (this.itemList != null && position < this.itemList.size()) {
            this.itemList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, this.itemList.size());
        }
    }

    public List<String> getItemList() {
        if (this.itemList == null) {
            return null;
        }

        return this.itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //RecyclerView는 ViewHolder를 새로 만들어야 할 때마다 이 메서드를 호출함.
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //RecyclerView는 ViewHolder를 데이터와 연결할 때 이 메서드를 호출한다. 이 메서드는 적절한 데이터를 가져와서 그 데이터를 사용하여 뷰 홀더의 레이아웃을 채워줌.
        String uri = itemList.get(position);



        Glide.with(mContext)
                .load(Uri.parse(uri))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .centerCrop()
                .dontAnimate()
                .into(holder.ivPhoto);

        //아이템 클릭시 호출하는 뷰.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemCallback(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        //리사이클러뷰의 아이탬 사이즈.
        return itemList == null ? 0 : itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_photo);

        }
    }
}
