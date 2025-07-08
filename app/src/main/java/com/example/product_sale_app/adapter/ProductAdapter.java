package com.example.product_sale_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.product_sale_app.R;
import com.example.product_sale_app.model.product.Product;
import com.example.product_sale_app.ui.product.ProductDetailActivity;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Product> productList;
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Format price using the NumberFormat instance
        holder.textViewProductPrice.setText(currencyFormatter.format(product.getPrice()));

        List<String> imageUrls = product.getImageUrls();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            String firstImageUrl = imageUrls.get(0);
            if (firstImageUrl != null && !firstImageUrl.isEmpty()) {
                Glide.with(context).load(firstImageUrl)
                        .placeholder(R.drawable.ic_placeholder_image)
                        .error(R.drawable.ic_error_image)
                        .into(holder.imageViewProduct);
            } else {
                Glide.with(context).load(R.drawable.ic_error_image)
                        .into(holder.imageViewProduct);
            }
        } else {
            Glide.with(context).load(R.drawable.ic_error_image)
                    .into(holder.imageViewProduct);
        }

        // Sửa lỗi setText cho các TextView
        holder.textViewProductName.setText(product.getProductName());
        holder.textViewProductDescription.setText(product.getBriefDescription());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product", (CharSequence) product);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProduct;
        TextView textViewProductName;
        TextView textViewProductPrice;
        TextView textViewProductDescription;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            textViewProductName = itemView.findViewById(R.id.textViewProductName); // Đảm bảo ID khớp layout
            textViewProductPrice = itemView.findViewById(R.id.textViewProductPrice); // Đảm bảo ID khớp layout
            textViewProductDescription = itemView.findViewById(R.id.textViewProductBriefDescription); // Đảm bảo ID khớp layout
        }
    }
}