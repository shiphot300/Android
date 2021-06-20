package com.example.mainpage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class FirstAdapter extends BaseAdapter {

    private List<MainList> mData;
    private Map<String, Integer> mMainListImageMap;

    public FirstAdapter(List<MainList> data) {
        this.mData = data;


    }

    //데이터 정보의 개수
    @Override
    public int getCount() {
        return mData.size();
    }
    //몇번째에 어떤 아이템이 있는지 알려줌
    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }
    //데이터베이스 연관된 부분이 나중에 참고
    @Override
    public long getItemId(int position) {
        return position;
    }
    //리스트뷰를 표시할때 그 아이템의 각각표시되는 번째를 호출해서 화면에 보여줌
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mainlist_1, parent, false);

            ImageView listImage = convertView.findViewById(R.id.list_image);
            TextView TextName = convertView.findViewById(R.id.list_text);
            TextView mainText = convertView.findViewById(R.id.main_text);

            holder.listImage = listImage;
            holder.TextName = TextName;
            holder.mainText = mainText;

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }


        MainList MainList = mData.get(position);
        holder.TextName.setText(MainList.getName());
        holder.mainText.setText(MainList.getText());

        return convertView;
    }

    static class ViewHolder {
        ImageView listImage;
        TextView TextName;
        TextView mainText;
    }
}
