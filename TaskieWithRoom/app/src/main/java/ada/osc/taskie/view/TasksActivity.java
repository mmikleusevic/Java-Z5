package ada.osc.taskie.view;

import android.arch.persistence.room.Query;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import ada.osc.taskie.R;
import ada.osc.taskie.database.TaskDao;
import ada.osc.taskie.database.TaskRoomDatabase;
import ada.osc.taskie.model.Task;
import ada.osc.taskie.model.TaskPriority;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TasksActivity extends AppCompatActivity {

    private static final String TAG = TasksActivity.class.getSimpleName();
    private static final int REQUEST_NEW_TASK = 10;
    public static final String EXTRA_TASK = "task";

    //	TaskRepository mRepository = TaskRepository.getInstance();
    private TaskDao mTaskDao;

    TaskAdapter mTaskAdapter;

    @BindView(R.id.fab_tasks_addNew)
    FloatingActionButton mNewTask;
    @BindView(R.id.recycler_tasks)
    RecyclerView mTasksRecycler;

    TaskClickListener mListener = new TaskClickListener() {
        @Override
        public void onClick(Task task) {
            toastTask(task);
        }

        @Override
        public void onLongClick(final Task task) {
            AlertDialog.Builder alert = new AlertDialog.Builder(TasksActivity.this);
            alert.setTitle("Delete");
            alert.setMessage("Do you want to delete the item?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(TasksActivity.this,"Item was deleted.",Toast.LENGTH_SHORT).show();
                    mTaskDao.delete(task);
                    updateTasksDisplay();
                }
            });
            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(TasksActivity.this,"Item was not deleted.",Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                }
            });
            alert.show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        ButterKnife.bind(this);

        TaskRoomDatabase database = TaskRoomDatabase.getDatabase(this);
        mTaskDao = database.taskDao();

        setUpRecyclerView();
        updateTasksDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTasksDisplay();
    }

    private void setUpRecyclerView() {

        int orientation = LinearLayoutManager.VERTICAL;

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                this,
                orientation,
                false
        );

        RecyclerView.ItemDecoration decoration =
                new DividerItemDecoration(this, orientation);

        RecyclerView.ItemAnimator animator = new DefaultItemAnimator();

        mTaskAdapter = new TaskAdapter(mListener);

        mTasksRecycler.setLayoutManager(layoutManager);
        mTasksRecycler.addItemDecoration(decoration);
        mTasksRecycler.setItemAnimator(animator);
        mTasksRecycler.setAdapter(mTaskAdapter);
    }

    private void updateTasksDisplay() {
        List<Task> tasks = mTaskDao.getAllTasks();
        mTaskAdapter.updateTasks(tasks);
        for (Task t : tasks) {
            Log.d(TAG, t.getTitle());
        }
    }

    private void toastTask(Task task) {
        Toast.makeText(
                this,
                task.getTitle() + "\n" + task.getDescription(),
                Toast.LENGTH_SHORT
        ).show();
    }

    @OnClick(R.id.fab_tasks_addNew)
    public void startNewTaskActivity() {
        Intent newTask = new Intent();
        newTask.setClass(this, NewTaskActivity.class);
//        startActivityForResult(newTask, REQUEST_NEW_TASK);
        startActivity(newTask);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
     @Override
    public boolean onOptionsItemSelected(MenuItem item) {
             if (item.getItemId() == R.id.filter_high) {
                 Toast.makeText(this, "You Filtered by High Priority", Toast.LENGTH_SHORT).show();
                 TaskPriority priority = TaskPriority.HIGH;
                 List<Task> tasks = mTaskDao.getFilter(priority);
                 mTaskAdapter.updateTasks(tasks);
                 return false;
             } else if (item.getItemId() == R.id.filter_med) {
                 Toast.makeText(this, "You Filtered by Medium Priority", Toast.LENGTH_SHORT).show();
                 TaskPriority priority = TaskPriority.MEDIUM;
                 List<Task> tasks = mTaskDao.getFilter(priority);
                 mTaskAdapter.updateTasks(tasks);
                 return false;
             } else if (item.getItemId() == R.id.filter_low) {
                 Toast.makeText(this, "You Filtered by Low Priority", Toast.LENGTH_SHORT).show();
                 TaskPriority priority = TaskPriority.LOW;
                 List<Task> tasks = mTaskDao.getFilter(priority);
                 mTaskAdapter.updateTasks(tasks);
                 return false;
             } else {
                 return false;
             }
         }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        if (requestCode == REQUEST_NEW_TASK && resultCode == RESULT_OK) {
//            if (data != null && data.hasExtra(EXTRA_TASK)) {
//                Task task = (Task) data.getSerializableExtra(EXTRA_TASK);
//                mRepository.saveTask(task);
//                updateTasksDisplay();
//            }
//        }
//    }
}
