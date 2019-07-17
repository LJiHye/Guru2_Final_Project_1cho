package com.example.cho1.guru2_final_project_1cho.firebase;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cho1.guru2_final_project_1cho.R;
import com.example.cho1.guru2_final_project_1cho.activity.JoinActivity;
import com.example.cho1.guru2_final_project_1cho.bean.CommentBean;
import com.example.cho1.guru2_final_project_1cho.bean.ExBean;
import com.example.cho1.guru2_final_project_1cho.bean.FleaBean;
import com.example.cho1.guru2_final_project_1cho.bean.MemberBean;
import com.example.cho1.guru2_final_project_1cho.db.FileDB;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CommentAdapter extends BaseAdapter {
    private Context mContext;
    private List<CommentBean> mCommentList;
    private CommentBean mCommentBean;
    private static FleaBean mFleaBean;
    private static ExBean mExBean;

    public CommentAdapter(Context context, List<CommentBean> commentList) {
        mContext = context;
        mCommentList = commentList;
    }

    public static void setFleaBean(FleaBean fleaBean) {
        mFleaBean = fleaBean;
    }

    public static void setExBean(ExBean exBean) {
        mExBean = exBean;
    }

    public void setList(List<CommentBean> commentList) {
        mCommentList = commentList;
    }

    @Override
    public int getCount() { return mCommentList.size(); }

    @Override
    public Object getItem(int i) { return mCommentList.get(i); }

    @Override
    public long getItemId(int i) { return i; }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.view_comment_item, null);

        TextView txtComment = view.findViewById(R.id.txtComment);
        TextView txtCommentDate = view.findViewById(R.id.txtCommentDate);
        TextView txtCommentId = view.findViewById(R.id.txtCommentId);
        final ImageView imgCommentDelete = view.findViewById(R.id.imgCommentDelete);

        mCommentBean = mCommentList.get(i);

        txtComment.setText(mCommentBean.comment);
        txtCommentDate.setText(mCommentBean.date);
        txtCommentId.setText(mCommentBean.userId);

        imgCommentDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builer = new AlertDialog.Builder(mContext);
                builer.setTitle("삭제");
                builer.setMessage("삭제하시겠습니까?");
                builer.setNegativeButton("아니오", null);
                builer.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MemberBean loginMember = FileDB.getLoginMember(mContext);
                        if(TextUtils.equals(loginMember.memId, mCommentBean.userId)) {
                            if (mCommentBean.flag == 1) {
                                String guid = JoinActivity.getUserIdFromUUID(mFleaBean.userId);
                                FirebaseDatabase.getInstance().getReference().child("buy").child(guid).child(mFleaBean.id).child("comments").child(mCommentBean.id).removeValue();
                                notifyDataSetChanged();
                            } else if (mCommentBean.flag == 2) {
                                String guid = JoinActivity.getUserIdFromUUID(mFleaBean.userId);
                                FirebaseDatabase.getInstance().getReference().child("sell").child(guid).child(mFleaBean.id).child("comments").child(mCommentBean.id).removeValue();
                                notifyDataSetChanged();
                            } else if (mCommentBean.flag == 3) {
                                String guid = JoinActivity.getUserIdFromUUID(mExBean.userId);
                                FirebaseDatabase.getInstance().getReference().child("ex").child(guid).child(mExBean.id).child("comments").child(mCommentBean.id).removeValue();
                                notifyDataSetChanged();
                            }
                            Toast.makeText(mContext, "삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builer.create().show(); // 다이어로그 나타남
            }
        });

        return view;
    }
}
