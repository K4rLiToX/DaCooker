package es.android.dacooker.fragments;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import es.android.dacooker.R;
import es.android.dacooker.activities.StepsRecipeCooking;
import es.android.dacooker.models.StepModel;
import es.android.dacooker.services.SoundService;
import es.android.dacooker.utilities.NotificationsPush;

/**
 * A simple {@link Fragment} subclass.
 */
/*
    Fragment de la Activity StepsRecipeCooking. Cada uno de estos contendra informacion y temporizador
        de un paso y habra tantos como pasos tenga la receta que queramos cocinar
 */
public class StepRecipeFragment extends Fragment {

    StepModel step; //Paso que mostrara el fragment
    int total;  //Total de pasos de la receta

    StepsRecipeCooking parentActivity;  //Activity que contiene el fragment
    TextView stepOfTotal, stepDescription, crono;   //TextViews del fragment
    ImageView arrowBack, arrowNext; //Iconos para avanzar y retroceder en los pasos
    Button btnFinish, btnCancel;    //Botones Finish y Cancel (solo se mostraran en los fragment correspondientes)

    //Chrono: Elementos usados para el funcionamiento del timer
    ImageButton btnTimerPlay, btnTimerStop, btnTimerPause;  //Botones de accion del timer
    LinearLayout timerLayout;   //Layout del timer: solo mostrado si el paso require timer
    String stepTimeMillis;  //Timer que se actualizara cada segundo
    long totalDuration, timeRemaining = 0;  //Tiempo inicial y restante: por si pausamos el tiempo
    boolean isPaused, isStopped, musicOn, isCancel; //Booleanos para controlar el estado del timer

    //Notifications
    AlarmManager am;
    PendingIntent pending;

    //Constructores del Fragment
    public StepRecipeFragment() {
        // Required empty public constructor
    }

    public StepRecipeFragment(StepModel s, int t) {
        this.step = s;
        this.total = t;
    }

    //Inicializacion de Vistas
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_step_recipe, container, false);

        initParameters(v);  //Inicializar parametros Vista
        if(step.isRequiredTimer()) {    //Si requiere timer...
            initTimerParameters();  //Inicializamos parametros del timer
            initTimerButtons(); //Inicializamos botones del timer
        }
        setView();  //
        initButtons();  //Inicializamos resto de botones

        return v;
    }

    //Inicializar parametros principales de la vista
    private void initParameters(View v){
        this.stepOfTotal = v.findViewById(R.id.step_fragment_orderOfTotal);
        this.stepDescription = v.findViewById(R.id.step_fragment_description);
        this.arrowBack = v.findViewById(R.id.step_fragment_btnLeft);
        this.arrowNext = v.findViewById(R.id.step_fragment_btnRight);
        this.btnCancel = v.findViewById(R.id.step_cooking_btnCancel);
        this.btnFinish = v.findViewById(R.id.step_cooking_btnFinish);

        this.timerLayout = v.findViewById(R.id.step_fragment_timer_layout);
        this.btnTimerPlay = v.findViewById(R.id.step_fragment_btn_timer_start);
        this.btnTimerPause = v.findViewById(R.id.step_fragment_btn_timer_pause);
        this.btnTimerStop = v.findViewById(R.id.step_fragment_btn_timer_stop);
        this.crono = v.findViewById(R.id.step_cooking_chrono);
        isStopped = true;

        parentActivity = (StepsRecipeCooking) getActivity();
    }

    //CountdownTimer Initialization: seteamos los paramteros del timer
    private void initTimerParameters(){

        //Visibilidad y Duracion del Timer
        this.timerLayout.setVisibility(View.VISIBLE);
        long durationHours = TimeUnit.HOURS.toMillis(step.getTimerHour());
        long durationMinutes = TimeUnit.MINUTES.toMillis(step.getTimerMinute());
        this.totalDuration = durationHours + durationMinutes;

        //Tiempo Totaldel timer en MILLIS (unidad de medida del timer)
        this.stepTimeMillis = String.format(Locale.ENGLISH, "%02d : %02d : %02d",
                TimeUnit.MILLISECONDS.toHours(totalDuration),
                TimeUnit.MILLISECONDS.toMinutes(totalDuration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totalDuration)),
                TimeUnit.MILLISECONDS.toSeconds(totalDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalDuration)));

        this.crono.setText(this.stepTimeMillis);    //Seteamos el tiempo total en formato hh:mm:ss

        //Desactivamos pause y stop (timer no activo aun)
        this.btnTimerPause.setEnabled(false);
        this.btnTimerStop.setEnabled(false);
    }

    //Inicializamos botones del timer: play, pause, stop
    private void initTimerButtons(){

        //Boton Pause
        btnTimerPause.setOnClickListener(view -> {
            isPaused = true;    //Pause a true
            btnTimerPause.setEnabled(false);    //Esta pausado, asi que desactivamos el pause y...
            btnTimerPlay.setEnabled(true);      //...activamos el play (reanudar)
            am.cancel(pending); //cancelamos la notificacion de momento (si esta pausado, el usuario es consciente de ello)
            Toast.makeText(getActivity(), getString(R.string.step_cooking_timer_pause), Toast.LENGTH_SHORT).show();
        });

        //Boton detener
        btnTimerStop.setOnClickListener(view -> {

            if(!isStopped) {    //Si pulsamos stop sin acabar el timer...
                isStopped = true;   //stop true
                isPaused = false;   //pause true (por si le damos despues de pausar)
                crono.setText(stepTimeMillis);  //seteamos el texto al tiempo total inicial (reinicio)
                btnTimerPlay.setEnabled(true);  //activamos el play
                btnTimerPause.setEnabled(false);    //desactivamos el pause (esta parado, luego no podra pausarse)
                btnTimerStop.setEnabled(false);     //desactivamos el stop (ya esta parado)
                Toast.makeText(getActivity(), getString(R.string.step_cooking_timer_stop), Toast.LENGTH_SHORT).show();
                am.cancel(pending); //Cancelamos la notificaion
            } else {    // Si pulsamos stop habiendo finalizado el timer -> musica sonando
                this.stopMusic();   //paramos la musica
            }
        });

        //boton play - reanudar
        btnTimerPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTimerPlay.setEnabled(false); //desativamos el play
                btnTimerPause.setEnabled(true); //activamos el pause
                btnTimerStop.setEnabled(true);  //activamos el stop
                if (isStopped) {    //si le damos al play estando reiniciado el timer...
                    Toast.makeText(getActivity(), getString(R.string.step_cooking_timer_start), Toast.LENGTH_SHORT).show();
                    isStopped = false;  //quitamos el stop
                    createNewTimer(totalDuration);  //creamos un nuevo timer con la duracion total
                    initNotifications(totalDuration);   //permitimos las notificaciones

                } else if (isPaused) {  //Si lo pulsamos estando en pause
                    Toast.makeText(getActivity(), getString(R.string.step_cooking_timer_resume), Toast.LENGTH_SHORT).show();
                    isPaused = false;   //quitamos el pause
                    createNewTimer(timeRemaining);  //creamos timer con el tiempo restante
                    initNotifications(timeRemaining);   //permitimos notificaciones
                }
            }
        });

    }

    //Crea un nuevo timer con el tiempo pasado por parametro
    private void createNewTimer(long timeMillis){
        new CountDownTimer(timeMillis, 1000) {  //creamos timer qeu haga tick cada 1000ms (1seg)

            @Override
            public void onTick(long millisUntilFinished) {  //en cada tick
                if (isPaused || isStopped) cancel();    //si detecta que esta parado o pausado -> cancelamos el contador
                else {  //si sigue activo
                    //Convert millis to hours, minutes and seconds
                    String time = String.format(Locale.ENGLISH, "%02d : %02d : %02d",
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                    crono.setText(time);    //actualizamos el textview
                    timeRemaining = millisUntilFinished;    //tiempo restante es igual al tiempo hasta acabar
                }
            }

            @Override
            public void onFinish() {    //cuando el timer acaba
                crono.setText(getString(R.string.step_recipe_finished));
                isStopped = true;   //detenemos el timer
                btnTimerPause.setEnabled(false);    //desactvamos pause y play (el timer ha acabado)
                btnTimerPlay.setEnabled(false);

                //Hacemos un intent para iniciar el servicio con musica y vibracion
                Intent finished = new Intent(getActivity(), SoundService.class);
                getActivity().startService(finished);
                musicOn = true; //musica esta sonando
            }
        }.start();  //comienza el timer
    }

    //Inicializacion de botones de la view (visibilidad)
    @SuppressLint({"SetTextI18n"})
    private void setView(){

        //Si el paso es el ultimo, mostramos el final y desaparece la flecha para ir al siguiente paso
        if(this.step.getStepOrder() == total) {
            this.btnFinish.setVisibility(View.VISIBLE);
            this.arrowNext.setVisibility(View.GONE);
        }

        //Si el paso es el ultimo, mostramos el cancelar (el usuario puede haberse equivocado)
            // y desaparece la flecha para ir al anterior paso
        if(this.step.getStepOrder() == 1) {
            this.btnCancel.setVisibility(View.VISIBLE);
            this.arrowBack.setVisibility(View.GONE);
        }

        //Seteamosel texto superior del paso, asi como la instruccion-descricion
        this.stepOfTotal.setText(getString(R.string.step_cooking_stepOfTotal_one) + " "
                + step.getStepOrder() + " " + getString(R.string.step_cooking_stepOfTotal_two)
                + " " + this.total);
        this.stepDescription.setText(this.step.getDescription());
    }

    //Inicializacion de botones de la view (funcionamiento)
    private void initButtons(){

        //Ir al paso anterior
        arrowBack.setOnClickListener(view -> {
            this.parentActivity.goBackStep();   //llama al metodo de la activity padre para que avance en el paso
            this.stopMusic();   //detiene la musica si estuviera sonando (usuario nervioso)
        });

        //Ir al paso siguiente
        arrowNext.setOnClickListener(view -> {
            this.parentActivity.goNextStep(); //llama al metodo de la activity padre para que retroceda
            this.stopMusic(); //detiene la musica si estuviera sonando (usuario nervioso)
        });

        //Cancelar el cocinado
        btnCancel.setOnClickListener(view -> {
            this.stopMusic(); //detiene la musica si estuviera sonando (usuario nervioso)
            isCancel = true;    //La receta es cancelada: no suma un cocinada al contador de la receta
            parentActivity.finish();    //Finaliza la actividad padre (vuelve al Details)
        });

        //Boton finalizar el cocinado
        btnFinish.setOnClickListener(view -> {
            if(!parentActivity.checkTimers())   //Si hay timer activo en alguno de los pasos, pedimos que el usuario lo detenga
                Toast.makeText(getActivity(), getString(R.string.step_cooking_stopped_timers_err), Toast.LENGTH_SHORT).show();
            else this.parentActivity.finishCooking();   //Si ninguno activo, finalizamos el cocinado y aumentamos las veces del cocinado en uno
            this.stopMusic(); //detiene la musica si estuviera sonando (usuario nervioso)
        });
    }

    //Utilities

    //Metodo usado por la activity padre para cmprobar si el timer esta o no activo
    public boolean isActive(){
        return isStopped;
    }

    //metodo para crear-generar notifiaciones
    public void initNotifications(long millis){
        //Intent que genera notifiacion dado el contexto inicial
        Intent intent = new Intent(getContext(), NotificationsPush.class);
        pending = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);

        //AlarmManager permite gestionar las diferentes notifiaciones generadas
        am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        //Setea la notificacion en un periodo que, desde este momento exactp (nowTime) mande una notifacion
            // en millis ms (el tiempo restante en el timer) -> solo cuando la app esta en background
        long nowTime = System.currentTimeMillis();
        am.set(AlarmManager.RTC_WAKEUP, nowTime + millis, pending);
    }

    //metodo usado para detener la musica
    private void stopMusic(){
        if(musicOn) {   //si la musica esta activa
            Intent stopMusic = new Intent(getActivity(), SoundService.class);
            getActivity().stopService(stopMusic);   //usamos el servicio para detenerla
            musicOn = false;    //musica stop
        }
    }

    @Override
    public void onPause(){  //Si nos salimos de la app, mostramos un toast de aviso
        super.onPause();
        if(!isCancel) Toast.makeText(getContext(), getString(R.string.cooking_do_not_close), Toast.LENGTH_LONG).show();
    }
}