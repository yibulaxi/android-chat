package cn.wildfire.chat.kit.conversation;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import cn.wildfire.chat.kit.R;
import cn.wildfire.chat.kit.WfcBaseActivity;

/**
 * Desc 创建智能体
 */
public class CreateRobotActivity extends WfcBaseActivity {

    private int sex = 1;     //1男 0女
    private int privacy = 1; //1公开 0私密

    @Override
    protected int contentLayout() {
        return R.layout.conversation_create_robot_activity;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RadioGroup rgSex = findViewById(R.id.rgSex);
        RadioGroup rgRange = findViewById(R.id.rgRange);
        TextView tvRandom = findViewById(R.id.tvRandom);
        TextView tvConfirm = findViewById(R.id.tvConfirm);

        rgSex.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            if (checkedId == R.id.rBtnGirl) {
                sex = 0;
            }
        });
        rgRange.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            if (checkedId == R.id.rBtnPrivate) {
                privacy = 0;
            }
        });

        tvRandom.setOnClickListener(v -> createRandomDesc());
        tvConfirm.setOnClickListener(v -> createRobot());
    }

    private void createRandomDesc() {
        Toast.makeText(this, "网络请求中", Toast.LENGTH_SHORT).show();
    }

    private void createRobot() {
        EditText etName = findViewById(R.id.etName);
        EditText etDesc = findViewById(R.id.etDesc);
        EditText etPrologue = findViewById(R.id.etPrologue);

        if (TextUtils.isEmpty(etName.getText())) {
            Toast.makeText(this, "请输入智能体名字", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(etDesc.getText())) {
            Toast.makeText(this, "请设定描述", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(etPrologue.getText())) {
            Toast.makeText(this, "请输入开场白", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "网络请求中", Toast.LENGTH_SHORT).show();

    }
}
