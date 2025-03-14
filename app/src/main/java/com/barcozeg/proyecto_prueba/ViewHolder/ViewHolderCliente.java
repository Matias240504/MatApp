package com.barcozeg.proyecto_prueba.ViewHolder;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolderCliente extends RecyclerView.ViewHolder{
    View mview;
    private ViewHolderCliente.clicklistener mclicklisnter;

    public interface clicklistener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public ViewHolderCliente(@NonNull View itemView) {
        super(itemView);
        mview = itemView;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mclicklisnter.onItemClick(view,getAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view) {
                mclicklisnter.onItemLongClick(view,getAdapterPosition());
                return false;
            }
        });
    }
}