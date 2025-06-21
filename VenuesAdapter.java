// VenuesAdapter.java
package com.example.wedwise_java;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.wedwise_java.R;
import java.util.List;

public class VenuesAdapter extends RecyclerView.Adapter<VenuesAdapter.VenueViewHolder> {

    private List<Venue> venues;
    private OnVenueClickListener listener;

    public interface OnVenueClickListener {
        void onVenueClick(Venue venue);
    }

    public VenuesAdapter(List<Venue> venues, OnVenueClickListener listener) {
        this.venues = venues;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VenueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_venue, parent, false);
        return new VenueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VenueViewHolder holder, int position) {
        Venue venue = venues.get(position);
        holder.bind(venue);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVenueClick(venue);
            }
        });
    }

    @Override
    public int getItemCount() {
        return venues != null ? venues.size() : 0;
    }

    public void updateVenues(List<Venue> newVenues) {
        this.venues = newVenues;
        notifyDataSetChanged();
    }

    static class VenueViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageVenue;
        private TextView textVenueName;
        private TextView textVenueType;
        private TextView textVenueLocation;
        private TextView textVenueCapacity;
        private TextView textVenuePrice;
        private TextView textVenueRating;
        private TextView textVenueDescription;
        private View availabilityIndicator;

        public VenueViewHolder(@NonNull View itemView) {
            super(itemView);
            imageVenue = itemView.findViewById(R.id.imageVenue);
            textVenueName = itemView.findViewById(R.id.textVenueName);
            textVenueType = itemView.findViewById(R.id.textVenueType);
            textVenueLocation = itemView.findViewById(R.id.textVenueLocation);
            textVenueCapacity = itemView.findViewById(R.id.textVenueCapacity);
            textVenuePrice = itemView.findViewById(R.id.textVenuePrice);
            textVenueRating = itemView.findViewById(R.id.textVenueRating);
            textVenueDescription = itemView.findViewById(R.id.textVenueDescription);
            availabilityIndicator = itemView.findViewById(R.id.availabilityIndicator);
        }

        public void bind(Venue venue) {
            if (venue == null) return;

            textVenueName.setText(venue.getName() != null ? venue.getName() : "Unknown Venue");
            textVenueType.setText(venue.getType() != null ? venue.getType() : "");
            textVenueLocation.setText(venue.getLocation() != null ? venue.getLocation() : "");
            textVenueCapacity.setText(venue.getCapacityText() != null ? venue.getCapacityText() : "");
            textVenuePrice.setText(venue.getFormattedPrice() != null ? venue.getFormattedPrice() : "");
            textVenueRating.setText(venue.getRatingText() != null ? venue.getRatingText() : "");
            textVenueDescription.setText(venue.getDescription() != null ? venue.getDescription() : "");

            // Set availability indicator
            if (availabilityIndicator != null) {
                if (venue.isAvailable()) {
                    availabilityIndicator.setBackgroundColor(
                            itemView.getContext().getResources().getColor(android.R.color.holo_green_light)
                    );
                } else {
                    availabilityIndicator.setBackgroundColor(
                            itemView.getContext().getResources().getColor(android.R.color.holo_red_light)
                    );
                }
            }

            // Load image using your preferred image loading library (Glide, Picasso, etc.)
            // For now, setting a placeholder
            if (imageVenue != null) {
                imageVenue.setImageResource(R.drawable.ic_venues);

                // Example with Glide (uncomment if you're using Glide):
                // if (venue.getImageUrl() != null && !venue.getImageUrl().isEmpty()) {
                //     Glide.with(itemView.getContext())
                //         .load(venue.getImageUrl())
                //         .placeholder(R.drawable.ic_venues)
                //         .error(R.drawable.ic_venues)
                //         .into(imageVenue);
                // }
            }
        }
    }
}