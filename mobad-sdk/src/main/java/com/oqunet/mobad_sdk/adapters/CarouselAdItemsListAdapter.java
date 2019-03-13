package com.oqunet.mobad_sdk.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.oqunet.mobad_sdk.R;
import com.oqunet.mobad_sdk.database.entity.CarouselAdItem;
import com.oqunet.mobad_sdk.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;


public class CarouselAdItemsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CarouselAdItem> carouselAdItemsList;
    LayoutInflater mInflater;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;


    public CarouselAdItemsListAdapter(Context mContext) {
        this.mContext = mContext;

    }

    public void setCarouselAdItemsList(List<CarouselAdItem> carouselAdItemsList) {
        this.carouselAdItemsList = carouselAdItemsList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView headline, description;
        private TextView buttonCallToAction;
        private ImageView image;

        ViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.image);
            headline = (TextView) itemView.findViewById(R.id.headline);
            description = (TextView) itemView.findViewById(R.id.description);
            buttonCallToAction = (TextView) itemView.findViewById(R.id.btn_cta);

        }

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_carousel_ad_items_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        ViewHolder itemViewHolder = (ViewHolder) holder;
        final CarouselAdItem carouselAdItem = carouselAdItemsList.get(position);

        ImageUtil.displayImage(itemViewHolder.image, carouselAdItem.getImage(), null);
        itemViewHolder.headline.setText(carouselAdItem.getTitle());
        itemViewHolder.description.setText(carouselAdItem.getDescription());
        itemViewHolder.buttonCallToAction.setText(carouselAdItem.getButtonName());
        itemViewHolder.buttonCallToAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, carouselAdItem, position);
                }

            }
        });



    }


    @Override
    public int getItemCount() {
        return carouselAdItemsList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, CarouselAdItem carouselAdItem, int position);

    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }
}
