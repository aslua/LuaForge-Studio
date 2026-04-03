package io.github.rosemoe.sora.widget.component;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.rosemoe.sora.R;

public class TextActionAdapter extends RecyclerView.Adapter<TextActionAdapter.ViewHolder> {

    public interface OnButtonClickListener {
        void onButtonClick(int buttonId);
    }

    private List<TextActionButton> buttons;
    private final OnButtonClickListener listener;
    private Context context;
    private int iconColor = 0xFF000000; // 默认黑色

    public TextActionAdapter(List<TextActionButton> buttons, OnButtonClickListener listener) {
        this.buttons = buttons;
        this.listener = listener;
    }

    public void setIconColor(int color) {
        this.iconColor = color;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.text_action_button_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextActionButton button = buttons.get(position);

        // 应用图标颜色
        holder.imageButton.setColorFilter(new PorterDuffColorFilter(
                iconColor, PorterDuff.Mode.SRC_ATOP
        ));

        holder.imageButton.setImageResource(button.getIconRes());
        holder.imageButton.setContentDescription(context.getString(button.getContentDescription()));
        holder.imageButton.setEnabled(button.isEnabled());
        holder.imageButton.setVisibility(button.isVisible() ? View.VISIBLE : View.GONE);
        holder.imageButton.setTag(button.getId());
        holder.imageButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onButtonClick(button.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return buttons.size();
    }

    public void updateButtons(List<TextActionButton> newButtons) {
        this.buttons = newButtons;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton imageButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageButton = itemView.findViewById(R.id.action_button);
        }
    }
}