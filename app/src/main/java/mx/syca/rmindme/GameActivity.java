package mx.syca.rmindme;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class GameActivity extends Activity {

    ArrayList<Pair<String,String>> mapItems = new ArrayList<>();


    AsyncTask<Void,Void,Void> taskParseItems ;

    private final static String [] SEPARATORS = new String[]{":","-"};

    private final static String LINE_FEED="\n";

    final ColorDrawable transparent = new ColorDrawable(Color.TRANSPARENT);

    final ColorDrawable correctAnswerColor = new ColorDrawable(R.color.right);

    final ColorDrawable wrongAnswerColor = new ColorDrawable(R.color.wrong);

    TransitionDrawable transitionCorrect;
    TransitionDrawable transitionWrong;

    Question currentQuestion  = new Question();

    int currentIndex = -1 ;

    private final static int _ONE_SECOND_IN_MILISECONDS = 1*1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        runTaskParseItems();
        transitionCorrect = new TransitionDrawable(new ColorDrawable[]{transparent,correctAnswerColor});
        transitionWrong = new TransitionDrawable(new ColorDrawable[]{transparent,wrongAnswerColor});

    }

    private void runTaskParseItems() {
        taskParseItems = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                return fillMapWithItemsFromFile();

            }

            @Override
            protected void onPostExecute(Void aVoid) {
                showNextQuestion();
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Void fillMapWithItemsFromFile(){

        try
        {

            BufferedReader inputReader = new BufferedReader(new InputStreamReader(
                    openFileInput(getString(R.string.file_name))));
            String inputString;


            while ((inputString = inputReader.readLine()) != null)
            {

                Pair <String,String> keyValue = tryGetKeyValue(inputString);

                if(keyValue!=null){

                    mapItems.add(keyValue);
                }


            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Pair<String,String> tryGetKeyValue(String line){

        Pair<String,String> parseResult = null;

        if( line == null || line.equals(LINE_FEED)  ){
            return null;
        }

        int indexOfCharSeparator = positionOfCharSeparator(line);

        if(indexOfCharSeparator == 0 )
            return null;


        try {
            String key = line.substring(0, indexOfCharSeparator);

            String value = line.substring(indexOfCharSeparator, line.length());

            parseResult = new Pair<>(key, value);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return parseResult;

    }

    private int positionOfCharSeparator (String line){

        for(String charSeparator: SEPARATORS){
            if(line.contains(charSeparator)){
                return  line.indexOf(charSeparator);
            }
        }
        return  0;

    }

   public void onClickAnswer(final View view){


       int indexSelectedAnswer = getIndexSelected(view);

       showAnswer(indexSelectedAnswer, view);

       view.postDelayed(new Runnable() {
           @Override
           public void run() {
               removeTransitions(view, getViewIndex(currentQuestion.indexCorrectAnswer));
               showNextQuestion();
           }
       }, _ONE_SECOND_IN_MILISECONDS*2);
       

   }

    private void removeTransitions(View viewSelected, View correct) {


        int indexSelectedAnswer = getIndexSelected(viewSelected);
        //transitionCorrect.reverseTransition(_ONE_SECOND_IN_MILISECONDS);
        viewSelected.setBackground(getDrawable(R.drawable.rounded_corner));
        if(indexSelectedAnswer != currentQuestion.indexCorrectAnswer) {

            correct.setBackground(getDrawable(R.drawable.rounded_corner));
            //transitionWrong.reverseTransition(_ONE_SECOND_IN_MILISECONDS);

        }


    }

    private void showNextQuestion() {

        currentIndex ++;


        if(currentIndex == mapItems.size())
        {
            finish();
            return;
        }

        Pair<String,String> currentPair = mapItems.get(currentIndex);

        currentQuestion.title = currentPair.first;

        ((TextView)findViewById(R.id.question_title)).setText(currentQuestion.title);

        currentQuestion.indexCorrectAnswer = getIndexAnswerRight();

        fillAnswerText(currentQuestion.indexCorrectAnswer,currentPair.second);

        fillWrongAnswer(currentQuestion.indexCorrectAnswer);


    }

    private void fillWrongAnswer(int indexCorrectAnswer) {

        switch (indexCorrectAnswer){

            case 1 :
                fillAnswerText(2,fakeAnswer());
                fillAnswerText(3,fakeAnswer());
                fillAnswerText(4,fakeAnswer());
                break;
            case 2 :
                fillAnswerText(1,fakeAnswer());
                fillAnswerText(3,fakeAnswer());
                fillAnswerText(4,fakeAnswer());
                break;
            case 3 :
                fillAnswerText(1,fakeAnswer());
                fillAnswerText(2,fakeAnswer());
                fillAnswerText(4,fakeAnswer());
                break;
            case 4 :
                fillAnswerText(1,fakeAnswer());
                fillAnswerText(2,fakeAnswer());
                fillAnswerText(3,fakeAnswer());
                break;

        }

    }

    private String fakeAnswer(){

        Random rand = new Random();
        int max = mapItems.size()-1;
        int min = 1;

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return mapItems.get(randomNum).second;

    }

    private void fillAnswerText(int index , String text){

        switch (index) {
            case 1 :
            case 2 :
            case 3 :
            case 4 :
                ((TextView) getViewIndex(index)).setText(text);
            default:return;
        }

    }

    private int getIndexAnswerRight(){

        Random rand = new Random();
        int max = 4;
        int min = 1;

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    private void showAnswer(int indexSelectedAnswer, View selectedView) {

        if(indexSelectedAnswer == currentQuestion.indexCorrectAnswer){

            selectedView.setBackgroundDrawable(getDrawable(R.color.right));

            //transitionCorrect.startTransition(_ONE_SECOND_IN_MILISECONDS);
        }

        else {
            //selectedView.setBackgroundDrawable(transitionWrong);

            //transitionWrong.startTransition(_ONE_SECOND_IN_MILISECONDS);

            selectedView.setBackgroundDrawable(getDrawable(R.color.wrong));
            getViewIndex(currentQuestion.indexCorrectAnswer).setBackgroundDrawable(getDrawable(R.color.right));

            //transitionWrong.startTransition(_ONE_SECOND_IN_MILISECONDS);

        }

    }

    private View getViewIndex(int index){

        switch (index){

            case 1: return findViewById(R.id.answer_1);
            case 2: return findViewById(R.id.answer_2);
            case 3: return findViewById(R.id.answer_3);
            case 4: return findViewById(R.id.answer_4);
            default:return null;
        }


    }

    private int getIndexSelected(View view) {
        switch (view.getId()){
            case R.id.answer_1: return 1;
            case R.id.answer_2: return 2;
            case R.id.answer_3: return 3;
            case R.id.answer_4: return 4;
            default:return 99;
        }
    }

    class Question{
        String title;
        String answer_1;
        String answer_2;
        String answer_3;
        String answer_4;
        int indexCorrectAnswer;
    }


}
