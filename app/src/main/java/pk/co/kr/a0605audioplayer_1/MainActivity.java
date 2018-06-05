package pk.co.kr.a0605audioplayer_1;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //노래 제목을 저장할 리스트
    List<String> list;
    //재생 준인 노래의 인덱스
    int idx;
    //노래를 재생할 미디어 플레이어
    MediaPlayer mediaPlayer;
    //노래 재생을 위한 버튼
    Button playbtn;
    //노래 제목을 출력할 텍스트 뷰
    TextView textView;
    //재생 중인 노래의 재생 위치를 보여줄 시크바
    SeekBar seekBar;
    //노래 재생 여부를 저장할 변수
    boolean isPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //재생할 노래 목록 만들기
        list = new ArrayList<>();
        list.add("http://192.168.0.250:8080/song/TTL.mp3");
        list.add("http://192.168.0.250:8080/song/빨간맛.mp3");
        list.add("http://192.168.0.250:8080/song/썸탈꺼야야.mp3");

        //재생할 노래의 인덱스를 0으로 설정
        idx = 0;

        //노래 재생기 객체 생성
        mediaPlayer = new MediaPlayer();

        //재생 중인 노래 제목을 출력할 텍스트 뷰 가져오기
        textView = (TextView) findViewById(R.id.textView);
        //노래 재생 버튼 가져오기
        playbtn = (Button) findViewById(R.id.playbtn);
        //노래 재생 위치를 출력할 시크바 가져오기
        seekBar = (SeekBar) findViewById(R.id.seekbar);

        isPlaying = false;

        //이벤트 핸들러 등록
        //노래 재생이 완료되면 호출되는 핸들러 등록
        mediaPlayer.setOnCompletionListener(onCompletionListener);
        //노래 재생 중 위치 변경이 일어났을 때 호출되는 핸들러
        mediaPlayer.setOnSeekCompleteListener(onSeekCompletionListener);
        //시크바에서 시크바의 값이 변경 되었을 때 호출되는 핸들러 등록
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

        //핸들러 호출
        handler.sendEmptyMessageDelayed(0, 200);

        //버튼들의 이벤트 핸들러 등록
        playbtn.setOnClickListener(onClickListener);
        Button stopBtn = (Button) findViewById(R.id.stopbtn);
        stopBtn.setOnClickListener(onClickListener);
        Button prevBtn = (Button) findViewById(R.id.stopbtn);
        prevBtn.setOnClickListener(onClickListener);
        Button nextBtn = (Button) findViewById(R.id.stopbtn);
        nextBtn.setOnClickListener(onClickListener);

        //첫번째 노래 준비
        loadMedia(idx);
    }

    //액티비티가 종료될 때 호출되는 메소드 재정의
    @Override
    public void onDestroy() {
        //상위 클래스의 메소드를 재정의할 때 상위 클래스의 메소드가
        //abstract(추상)메소드가 아니라면 반드시 호출해 주어야 합니다.
        super.onDestroy();
        //미디어 플레이어가 null이 아니라면 메모리 해체
        //미디어 플레이어는 시스템 자원이라서 모든 애플리케이션이 공유하기 때문에
        //다른 애플리케이션이 사용할 수 있도록 정리를 해주어야 합니다.
        if (mediaPlayer != null)
            mediaPlayer.release();
    }

    //노래 재생 준비를 해주는 메소드
    private void loadMedia(int idx) {
        try {
            //재생할 노래를 설정
            mediaPlayer.setDataSource(this, Uri.parse(list.get(idx)));
            //텍스트 뷰에 노래 제목 설정
            textView.setText(list.get(idx));
            //재생 시간을 시크바의 최대 값으로 설정
            seekBar.setMax(mediaPlayer.getDuration());
            //재생준비
            mediaPlayer.prepare();
        } catch (Exception e) {}
    }

    //버튼의 클릭 이벤트 처리를 위한 객체를 생성
    Button.OnClickListener onClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            //버튼이 여러 개 일 때는 v의 getId로 구분
            switch (v.getId()) {
                case R.id.playbtn:
                    if (isPlaying == false) {
                        mediaPlayer.start();
                        playbtn.setText("멈춤");
                    } else {
                        mediaPlayer.pause();
                        playbtn.setText("재생");
                    }
                    break;
                case R.id.stopbtn:
                    mediaPlayer.stop();
                    playbtn.setText("재생");
                    seekBar.setProgress(0);
                    try {
                        mediaPlayer.prepare();
                    } catch (Exception e) {}
                    break;
                case R.id.prevbtn:
                    idx = (idx == 0 ? list.size() - 1 : idx - 1);
                    mediaPlayer.reset();
                    loadMedia(idx);
                    mediaPlayer.start();
                    playbtn.setText("멈춤");
                    break;
                case R.id.nextbtn:
                    idx = (idx == list.size() - 1 ? 0 : idx + 1);
                    mediaPlayer.reset();
                    loadMedia(idx);
                    mediaPlayer.start();
                    playbtn.setText("멈춤");
                    break;
            }
        }
    };

    //재생이 종료되었을 때 이벤트 처리 객체
    MediaPlayer.OnCompletionListener onCompletionListener =
            new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    idx = (idx == list.size() - 1 ? 0 : idx + 1);
                    mediaPlayer.reset();
                    loadMedia(idx);
                    mediaPlayer.start();
                }
            };
    //노래 재생위치가 이동되었을 때 처리할 이벤트 핸들러 작성
    MediaPlayer.OnSeekCompleteListener onSeekCompletionListener =
            new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    if(isPlaying){
                        mediaPlayer.start();
                    }
                }
            };
    //시크바의 값을 변경했을 때 처리할 이벤트 핸들러 작성
    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar,
                                              int progress, boolean fromUser) {
                    //유저가 손을 땠을 때
                    if(fromUser){
                        mediaPlayer.seekTo(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    isPlaying = mediaPlayer.isPlaying();
                    if(isPlaying)
                        mediaPlayer.pause();
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            };

    Handler handler = new Handler(){
        public void handleMessage(Message message){
            if(mediaPlayer != null && mediaPlayer.isPlaying()){
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
            }
            handler.sendEmptyMessageDelayed(0, 200);
        }
    };
}











