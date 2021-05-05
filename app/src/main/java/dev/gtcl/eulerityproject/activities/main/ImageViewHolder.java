package dev.gtcl.eulerityproject.activities.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import dev.gtcl.eulerityproject.R;
import dev.gtcl.eulerityproject.models.ImageURL;

public class ImageViewHolder extends RecyclerView.ViewHolder {
    private final ImageView imageView;
    private final CardView cardView;

    private ImageViewHolder(View view){
        super(view);
        imageView = view.findViewById(R.id.photo_thumbnail);
        cardView = view.findViewById(R.id.photo_card_view);
    }

    public void bind(ImageURL image, ItemClickListener itemClickListener){
        Glide.with(imageView.getContext())
                .load(image.getUrl())
                .apply((new RequestOptions())
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image))
                .into(imageView);
        cardView.setOnClickListener(view -> itemClickListener.onItemClicked(image.getUrl()));
    }

    public static ImageViewHolder create(ViewGroup viewGroup){
        return new ImageViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_image, viewGroup, false));
    }
}
