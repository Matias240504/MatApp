package com.barcozeg.proyecto_prueba.ViewHolder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.barcozeg.proyecto_prueba.R;

public class ViewHolderCliente extends RecyclerView.ViewHolder{
    View mview;
    private ViewHolderCliente.clicklistener mclicklisnter;

    public interface clicklistener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnClickListener(ViewHolderCliente.clicklistener clickListener){
        mclicklisnter = clickListener;
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

    public void setearDatosCliente(Context context, String id_cliente, String uid_cliente,
                                   String nombres, String apellidos, String correo,
                                   String dni, String telefono, String direccion) {

        TextView tvidclientel = mview.findViewById(R.id.tvidclientei);
        TextView tvuidclientel = mview.findViewById(R.id.tvuidclientei);
        TextView tvnombrel = mview.findViewById(R.id.tvnombrei);
        TextView tvapellidol = mview.findViewById(R.id.tvapellidoi);
        TextView tvcorreoclientel = mview.findViewById(R.id.tvcorreoclientei);
        TextView tvdniclientel = mview.findViewById(R.id.tvdniclientei);
        TextView tvtelefonoclientel = mview.findViewById(R.id.tvtelefonoclientei);
        TextView tvdireccionclientel = mview.findViewById(R.id.tvdireccionclientei);

        tvidclientel.setText(id_cliente);
        tvuidclientel.setText(uid_cliente);
        tvnombrel.setText(nombres);
        tvapellidol.setText(apellidos);
        tvcorreoclientel.setText(correo);
        tvdniclientel.setText(dni);
        tvtelefonoclientel.setText(telefono);
        tvdireccionclientel.setText(direccion);
    }
}