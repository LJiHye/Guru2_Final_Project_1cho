package com.example.cho1.guru2_final_project_1cho.firebase;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cho1.guru2_final_project_1cho.R;
import com.example.cho1.guru2_final_project_1cho.bean.CommentBean;

import java.util.List;

public class CommentAdapter extends BaseAdapter {
    private Context mContext;
    private List<CommentBean> mCommentList;

    public CommentAdapter(Context context, List<CommentBean> commentList) {
        mContext = context;
        mCommentList = commentList;
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
        ImageView imgCommentDelete = view.findViewById(R.id.imgCommentDelete);

        final CommentBean commentBean = mCommentList.get(i);

        txtComment.setText(commentBean.comment);
        txtCommentDate.setText(commentBean.date);
        txtCommentId.setText(commentBean.userId);

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
                       /* String email = FileDB.getLoginMember(mContext).memId;
                        String uuid = JoinActivity.getUserIdFromUUID(email);
                        // DB에서 삭제 처리
                        // 3단계로 내려 가서 특정 메모만 지워야 함. 1단계는 전체 유저 메모, 2단계는 해당 유저의 전체 메모
                        FirebaseDatabase.getInstance().getReference().child("comment").child(uuid).child(commentBean.id).removeValue();*/
                        Toast.makeText(mContext, "삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                builer.create().show(); // 다이어로그 나타남
            }
        });

        return view;
    }
}
