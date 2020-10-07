package com.example.calldetect.messageriePrincipale.messageCustomiser;

import android.app.AlertDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calldetect.R;
import com.example.calldetect.messageriePrincipale.ModelMessagePrinc;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterMessagePersonnaliser extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ModelMessagePrinc> list;
    private Context context;
    private LayoutInflater layoutInflater;
    public ImageView msg_envoye, msg_recue, echec_envoi_msg;
    public ProgressBar envoi_en_cour;
    public Activity_message_personaliser activity_message_personaliser;
    public LinearLayout linearLayoutR, linearLayoutE;
    public CircleImageView imgSelectMsgRecue,imgSelectMsgEnvoie;


    public AdapterMessagePersonnaliser(List<ModelMessagePrinc> list, Context context) {
        this.list = list;
        this.context = context;
        this.activity_message_personaliser = (Activity_message_personaliser) context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        // si c'est un messgae recue, alors :
        if (i==1){
            View view = layoutInflater.inflate(R.layout.item_msg_recue  , viewGroup, false);
            ViewHolderReceptionMsg viewHolderReceptionMsg = new ViewHolderReceptionMsg(view, activity_message_personaliser);
            return viewHolderReceptionMsg;
        }else {
            View view = layoutInflater.inflate(R.layout.item_msg_envoye  , viewGroup, false);
            ViewHolderEnvoiMgs viewHolderEnvoiMgs = new ViewHolderEnvoiMgs(view, activity_message_personaliser);
            return viewHolderEnvoiMgs;
        }
    }

    @Override
    public int getItemViewType(int position) {
        // si le message est recue
        if (list.get(position).getReceived()==1){
            return 1;
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
        //si message recue; alors :............
        if (list.get(i).getReceived()==1){
            TextView messageR, operateurR, heureR;

            final ViewHolderReceptionMsg viewHolderReceptionMsg = (ViewHolderReceptionMsg) viewHolder;

            messageR = viewHolderReceptionMsg.messageR;
            operateurR = viewHolderReceptionMsg.operateurR;
            heureR = viewHolderReceptionMsg.heureR;
            imgSelectMsgRecue = viewHolderReceptionMsg.imgSelectMsgRecue;
            linearLayoutR = viewHolderReceptionMsg.linearLayoutR;

            /**
             * ici on veut simplement faire une difference entre le msg selectionner et le reste ! en fait a la selection d'un mesge,
             * on rend visible un widget et a sa deselection on le rend invisible
             */
            linearLayoutR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (activity_message_personaliser.is_multiselection_active){
                        if (!list.get(i).isCheket()){

                            viewHolderReceptionMsg.imgSelectMsgRecue.setVisibility(View.VISIBLE);
                            activity_message_personaliser.selectionMessage(i);

                        }else {

                            viewHolderReceptionMsg.imgSelectMsgRecue.setVisibility(View.GONE);
                            activity_message_personaliser.selectionMessage(i);


                        }
                    }else{
                        AlertDialog.Builder mypopup = new AlertDialog.Builder(activity_message_personaliser);
                        LinearLayout linearLayout = new LinearLayout(activity_message_personaliser);
                        TextView repondre = new TextView(activity_message_personaliser);
                        TextView transferer = new TextView(activity_message_personaliser);
                        transferer.setText("Transferer");
                        linearLayout.addView(repondre);
                        linearLayout.addView(transferer);
                        mypopup.setView(linearLayout);
                        mypopup.show();
                    }
                }
            });


            messageR.setText(list.get(i).getMessage());
            operateurR.setText(list.get(i).getOperateurTelephonique());
            imgSelectMsgRecue.setVisibility(View.GONE);
            //on formate la date
            Date date = new Date(Long.valueOf(list.get(i).getJour()));

            String hour = (String) DateFormat.format("HH", date);
            String minute = (String) DateFormat.format("mm", date);
            heureR.setText(hour+" : "+minute);
        }else {
            TextView messageE, operateurE, heureE;

            final ViewHolderEnvoiMgs viewHolderEnvoiMgs = (ViewHolderEnvoiMgs) viewHolder;

            messageE = viewHolderEnvoiMgs.messageE;
            operateurE = viewHolderEnvoiMgs.operateurE;
            heureE = viewHolderEnvoiMgs.heureE;
            imgSelectMsgEnvoie = viewHolderEnvoiMgs.imgSelectMsgEnv;
            linearLayoutE = viewHolderEnvoiMgs.linearLayoutE;

            /**
             * ici on veut simplement faire une difference entre le msg selectionner et le reste ! en fait a la selection d'un mesge,
             * on rend visible un widget et a sa deselection on le rend invisible
             */
            linearLayoutE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activity_message_personaliser.is_multiselection_active){
                        if (!list.get(i).isCheket()){

                            viewHolderEnvoiMgs.imgSelectMsgEnv.setVisibility(View.VISIBLE);
                            activity_message_personaliser.selectionMessage(i);

                        }else {

                            viewHolderEnvoiMgs.imgSelectMsgEnv.setVisibility(View.GONE);
                            activity_message_personaliser.selectionMessage(i);

                        }
                    }else {

                    }
                }
            });

            msg_envoye = viewHolderEnvoiMgs.msg_envoye;
            msg_recue = viewHolderEnvoiMgs.msg_recue;
            echec_envoi_msg = viewHolderEnvoiMgs.echec_envoi_msg;
            envoi_en_cour = viewHolderEnvoiMgs.envoi_en_cour;



            messageE.setText(list.get(i).getMessage());
            operateurE.setText(list.get(i).getOperateurTelephonique());
            //on formate la date
            Date date = new Date(Long.valueOf(list.get(i).getJour()));

            String hour = (String) DateFormat.format("HH", date);
            String minute = (String) DateFormat.format("mm", date);
            heureE.setText(hour+" : "+minute);

            //on cache tout les imageView
            msg_recue.setVisibility(View.GONE);
            msg_envoye.setVisibility(View.GONE);
            echec_envoi_msg.setVisibility(View.GONE);

            imgSelectMsgEnvoie.setVisibility(View.GONE);

            if (list.get(i).getEchecEnvoie() == 1){
                envoi_en_cour.setVisibility(View.GONE);
                msg_recue.setVisibility(View.GONE);
                msg_envoye.setVisibility(View.GONE);
                echec_envoi_msg.setVisibility(View.VISIBLE);
            }else{
                if (list.get(i).getMsgEnvoye() == 1){
                    envoi_en_cour.setVisibility(View.GONE);
                    msg_recue.setVisibility(View.GONE);
                    msg_envoye.setVisibility(View.VISIBLE);
                    echec_envoi_msg.setVisibility(View.GONE);
                }
                if (list.get(i).getMsgBienRecue()==1){
                    envoi_en_cour.setVisibility(View.GONE);
                    msg_envoye.setVisibility(View.GONE);
                    echec_envoi_msg.setVisibility(View.GONE);
                    msg_recue.setVisibility(View.VISIBLE);
                }
            }



        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolderEnvoiMgs extends RecyclerView.ViewHolder{
       TextView messageE, operateurE, heureE;
        ImageView msg_envoye, msg_recue, echec_envoi_msg;
        ProgressBar envoi_en_cour;
        Activity_message_personaliser activity_message_personaliser;
        LinearLayout linearLayoutE;
        CircleImageView imgSelectMsgEnv;


       public ViewHolderEnvoiMgs(@NonNull View itemView, Activity_message_personaliser activity_message_personaliser) {
           super(itemView);
           messageE = itemView.findViewById(R.id.contenue_message);
           operateurE = itemView.findViewById(R.id.operateur);
           heureE = itemView.findViewById(R.id.heure);
           msg_envoye = itemView.findViewById(R.id.msg_envoye);
           msg_recue = itemView.findViewById(R.id.msg_recue);
           echec_envoi_msg = itemView.findViewById(R.id.echec_envoie_msg);
           envoi_en_cour = itemView.findViewById(R.id.envoi_en_cour);
           linearLayoutE = itemView.findViewById(R.id.layoutE);
           imgSelectMsgEnv = itemView.findViewById(R.id.selectionMsgEnv);
           this.activity_message_personaliser = activity_message_personaliser;
           linearLayoutE.setOnLongClickListener(activity_message_personaliser);

       }


    }

   public static class ViewHolderReceptionMsg extends RecyclerView.ViewHolder{
       TextView messageR, operateurR, heureR;
       LinearLayout linearLayoutR;
       Activity_message_personaliser activity_message_personaliser;
       CircleImageView imgSelectMsgRecue;
       public ViewHolderReceptionMsg(@NonNull View itemView, Activity_message_personaliser activity_message_personaliser) {
           super(itemView);
           messageR = itemView.findViewById(R.id.contenue_message);
           operateurR = itemView.findViewById(R.id.operateur);
           heureR = itemView.findViewById(R.id.heure);
           linearLayoutR = itemView.findViewById(R.id.layoutR);
           imgSelectMsgRecue = itemView.findViewById(R.id.selectionMsgRecue);
           this.activity_message_personaliser = activity_message_personaliser;
           linearLayoutR.setOnLongClickListener(activity_message_personaliser);


       }


   }
}
