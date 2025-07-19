package com.example.product_sale_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Log;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.product_sale_app.R;
import com.example.product_sale_app.model.product.Product;
import com.example.product_sale_app.ui.product.ProductDetailActivity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TopProductAdapter extends RecyclerView.Adapter<TopProductAdapter.TopProductViewHolder> {
    private List<Product> topProductList;
    private Context context;
    private NumberFormat currencyFormatter;

    public TopProductAdapter(Context context, List<Product> topProductList) {
        this.context = context;
        this.topProductList = topProductList != null ? topProductList : new ArrayList<>();
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    }

    @NonNull
    @Override
    public TopProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_card, parent, false);
        return new TopProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopProductViewHolder holder, int position) {
        Product product = topProductList.get(position);

        // Bind data to the views defined in item_product_card.xml
        holder.textViewProductName.setText(product.getProductName());

        if (product.getPrice() != null) {
            holder.textViewProductPrice.setText(currencyFormatter.format(product.getPrice()));
        } else {
            holder.textViewProductPrice.setText("N/A");
        }

        List<String> imageUrls = product.getImageUrls();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            String firstImageUrl = imageUrls.get(0); // Hiển thị ảnh đầu tiên
            if (firstImageUrl != null && !firstImageUrl.isEmpty()) {
                Glide.with(context)
                        .load(firstImageUrl)
                        .placeholder(R.drawable.ic_placeholder_image)
                        .error(R.drawable.ic_error_image)
                        .into(holder.imageViewProduct);
            } else {
                Glide.with(context)
                        .load(R.drawable.ic_error_image)
                        .into(holder.imageViewProduct);
            }
        } else {
            Glide.with(context)
                    .load(R.drawable.ic_error_image)
                    .into(holder.imageViewProduct);
        }

        // Thêm onClickListener để mở ProductDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product",  product); // Truyền toàn bộ Product, bao gồm imageUrls
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Cần nếu context không phải Activity
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return topProductList != null ? topProductList.size() : 0;
    }

    public void updateProducts(List<Product> newProducts) {
        this.topProductList.clear();
        if (newProducts != null) {
            this.topProductList.addAll(newProducts);
        }
        notifyDataSetChanged();
    }

    static class TopProductViewHolder extends RecyclerView.ViewHolder {
        com.google.android.material.imageview.ShapeableImageView imageViewProduct;
        TextView textViewProductName;
        TextView textViewProductPrice;
        ImageView imageViewAccessIcon;

        @OptIn(markerClass = UnstableApi.class)
        public TopProductViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewProduct = itemView.findViewById(R.id.top_product_image);
            textViewProductName = itemView.findViewById(R.id.top_product_name);
            textViewProductPrice = itemView.findViewById(R.id.top_product_price);
            imageViewAccessIcon = itemView.findViewById(R.id.top_product_access_icon);

            if (imageViewProduct == null) {
                Log.e("TopProductViewHolder", "imageViewProduct is null. Check ID in item_product_card.xml");
            }
            if (textViewProductName == null) {
                Log.e("TopProductViewHolder", "textViewProductName is null. Check ID in item_product_card.xml");
            }
            if (textViewProductPrice == null) {
                Log.e("TopProductViewHolder", "textViewProductPrice is null. Check ID in item_product_card.xml");
            }
        }
    }
}