package silbajuk.ch23.nerdlauncher

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "NerdLauncherActivity"

class NerdLauncherActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nerd_launcher)

        recyclerView = findViewById(R.id.app_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        setupAdapter()
    }

    private fun setupAdapter(){
        val startupIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val activities = packageManager.queryIntentActivities(startupIntent, 0)
        Log.i(TAG, "Found ${activities.size} activities")

        //액티비티의 라벨 알파벳순으로 정렬
        activities.sortWith(Comparator{a, b->
            String.CASE_INSENSITIVE_ORDER.compare(
                a.loadLabel(packageManager).toString(),
                b.loadLabel(packageManager).toString()
            )
        })

        recyclerView.adapter = ActivityAdapter(activities)
    }

    private class ActivityHolder(itemView: View):
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener{

        val nameTextView : TextView = itemView.findViewById(R.id.item_name_text_view)
        val iconImageView : ImageView = itemView.findViewById(R.id.item_icon_image_view)

        private lateinit var resolveInfo: ResolveInfo

        init{
           itemView.setOnClickListener(this)
        }

        fun bindActivity(resolveInfo: ResolveInfo) {
            this.resolveInfo = resolveInfo
            val packageManager = itemView.context.packageManager
            val appName = resolveInfo.loadLabel(packageManager).toString()
            val appIcon = resolveInfo.loadIcon(packageManager)
            nameTextView.text = appName
            iconImageView.setImageDrawable(appIcon)
        }

        override fun onClick(view: View?) {
            val activityInfo = resolveInfo.activityInfo

            val intent = Intent(Intent.ACTION_MAIN).apply{
                setClassName(activityInfo.applicationInfo.packageName,
                activityInfo.name)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val context = view?.context
            context?.startActivity(intent)
        }
    }

    private class ActivityAdapter(val activities: List<ResolveInfo>):RecyclerView.Adapter<ActivityHolder>(){
        override fun onCreateViewHolder(container: ViewGroup, viewType: Int): ActivityHolder {
            val layoutInflater = LayoutInflater.from(container.context)
            val view = layoutInflater.
                    inflate(R.layout.recycler_view_item, container, false)
            return ActivityHolder(view)
        }

        override fun onBindViewHolder(holder: ActivityHolder, position: Int) {
            val resolveInfo = activities[position]
            holder.bindActivity(resolveInfo)
        }

        override fun getItemCount(): Int {
            return activities.size
        }
    }
}