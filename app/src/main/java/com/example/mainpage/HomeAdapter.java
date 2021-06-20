package com.example.mainpage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

abstract public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private Context contexet;
    private List<Map<String, Object>> products;

    public HomeAdapter(Context context) {
        this.contexet = context;
        this.products = new ArrayList<>();
    }

    abstract public void onClickProduct(Map<String, Object> product);

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //RecyclerView는 ViewHolder를 새로 만들어야 할 때마다 이 메서드를 호출함.
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mainlist_1, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //RecyclerView는 ViewHolder를 데이터와 연결할 때 이 메서드를 호출한다. 이 메서드는 적절한 데이터를 가져와서 그 데이터를 사용하여 뷰 홀더의 레이아웃을 채워줌.

        Map<String, Object> product = products.get(position);

        holder.TextName.setText((String) product.get("title"));
        holder.mainText.setText((String) product.get("content"));
        holder.mainPrice.setText(String.format("%.0f", (double) product.get("price")) + "원");

        try {
            Glide.with(contexet).load("http://192.168.234.1:3000/" + (String) product.get("thumbnail")).into(holder.listImage);
        } catch (Exception e) {

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickProduct(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void update(List<Map<String, Object>> products) {
        this.products.clear();
        this.products.addAll(products);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView listImage;
        private TextView TextName;
        private TextView mainText;
        private TextView mainPrice;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            listImage = itemView.findViewById(R.id.list_image);
            TextName = itemView.findViewById(R.id.list_text);
            mainText = itemView.findViewById(R.id.main_text);
            mainPrice = itemView.findViewById(R.id.main_price);

        }
    }

}
