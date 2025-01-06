package com.example.choreharmony.views.assets

import android.content.Intent
import android.os.Build
import android.provider.CalendarContract
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.choreharmony.model.Chore

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChoreCalendar(chore: Chore) {
    val intent = Intent(Intent.ACTION_EDIT);

    intent.setType("vnd.android.cursor.item/event");
    intent.putExtra(CalendarContract.Events.TITLE, chore.name)
    intent.putExtra(CalendarContract.Events.ALL_DAY, true)
    intent.putExtra(CalendarContract.Events.DESCRIPTION, chore.name)
    if (chore.recurrence_in_days != null) {
        intent.putExtra(CalendarContract.Events.RRULE, "FREQ=DAILY;INTERVAL=${chore.recurrence_in_days}");
    }
    ContextCompat.startActivity(LocalContext.current, intent, null)
}
