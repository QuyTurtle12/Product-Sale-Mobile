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
    private int layoutResId;

    public ProductAdapter(Context context, List<Product> productList) {
        this(context, productList, R.layout.item_product);
    }

    public ProductAdapter(Context context, List<Product> productList, int layoutResId) {
        this.context = context;
        this.productList = productList;
        this.layoutResId = layoutResId;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new ProductViewHolder(view, layoutResId);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product, context);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product", product);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateProducts(List<Product> newProducts) {
        this.productList.clear();
        if (newProducts != null) {
            this.productList.addAll(newProducts);
        }
        notifyDataSetChanged();
    }

    public void addProducts(List<Product> additionalProducts) {
        if (additionalProducts != null) {
            int startPosition = this.productList.size();
            this.productList.addAll(additionalProducts);
            notifyItemRangeInserted(startPosition, additionalProducts.size());
        }
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewProduct;
        private TextView textViewProductName;
        private TextView textViewProductBriefDescription;
        private TextView textViewProductPrice;
        private int layoutResId;

        ProductViewHolder(View itemView, int layoutResId) {
            super(itemView);
            this.layoutResId = layoutResId;

            if (layoutResId == R.layout.item_product) {
                imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
                textViewProductName = itemView.findViewById(R.id.textViewProductName);
                textViewProductBriefDescription = itemView.findViewById(R.id.textViewProductBriefDescription);
                textViewProductPrice = itemView.findViewById(R.id.textViewProductPrice);
            } else if (layoutResId == R.layout.item_product_card) {
                imageViewProduct = itemView.findViewById(R.id.top_product_image);
                textViewProductName = itemView.findViewById(R.id.top_product_name);
                textViewProductPrice = itemView.findViewById(R.id.top_product_price);
                // Note: top product card doesn't have brief description
            }
        }

        void bind(Product product, Context context) {
            // Set product name and price for any layout
            if (textViewProductName != null) {
                textViewProductName.setText(product.getProductName());
            }

            if (textViewProductPrice != null) {
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                textViewProductPrice.setText(formatter.format(product.getPrice()));
            }

            // Set brief description only if the view exists
            if (textViewProductBriefDescription != null) {
                textViewProductBriefDescription.setText(product.getBriefDescription());
            }

            // Handle image loading
            if (imageViewProduct != null) {
                List<String> imageUrls = product.getImageUrls();
                if (imageUrls != null && !imageUrls.isEmpty()) {
                    String firstImageUrl = imageUrls.get(0);
                    if (firstImageUrl != null && !firstImageUrl.isEmpty()) {
                        Glide.with(context).load(firstImageUrl)
                                .placeholder(R.drawable.ic_placeholder_image)
                                .error(R.drawable.ic_error_image)
                                .into(imageViewProduct);
                    } else {
                        Glide.with(context).load(R.drawable.ic_error_image)
                                .into(imageViewProduct);
                    }
                } else {
                    Glide.with(context).load(R.drawable.ic_error_image)
                            .into(imageViewProduct);
                }
            }
        }
    }
}