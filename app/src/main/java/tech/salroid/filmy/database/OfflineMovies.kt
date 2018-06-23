package tech.salroid.filmy.database

/**
 * Created by BlackCat on 5/20/2018.
 */
import android.app.AlertDialog
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView

import java.util.HashMap

import tech.salroid.filmy.R
import tech.salroid.filmy.customs.CustomToast
import tech.salroid.filmy.utility.Constants



 class OfflineMovies(private val context:Context) {

 fun saveMovie(movieMap:HashMap<String, String>?, movie_id:String, movie_id_final:String, flag:Int) {

if (movieMap != null && !movieMap!!.isEmpty())
{


val saveValues = ContentValues()

saveValues.put(FilmContract.SaveEntry.SAVE_ID, movie_id)
saveValues.put(FilmContract.SaveEntry.SAVE_TITLE, movieMap!!["title"])
saveValues.put(FilmContract.SaveEntry.SAVE_TAGLINE, movieMap!!["tagline"])
saveValues.put(FilmContract.SaveEntry.SAVE_DESCRIPTION, movieMap!!["overview"])
saveValues.put(FilmContract.SaveEntry.SAVE_BANNER, movieMap!!["banner"])
saveValues.put(FilmContract.SaveEntry.SAVE_TRAILER, movieMap!!["trailer"])
saveValues.put(FilmContract.SaveEntry.SAVE_RATING, movieMap!!["rating"])
saveValues.put(FilmContract.SaveEntry.SAVE_YEAR, movieMap!!["year"])
saveValues.put(FilmContract.SaveEntry.SAVE_POSTER_LINK, movieMap!!["poster"])
saveValues.put(FilmContract.SaveEntry.SAVE_RUNTIME, movieMap!!["runtime"])
saveValues.put(FilmContract.SaveEntry.SAVE_CERTIFICATION, movieMap!!["certification"])
saveValues.put(FilmContract.SaveEntry.SAVE_LANGUAGE, movieMap!!["language"])
saveValues.put(FilmContract.SaveEntry.SAVE_RELEASED, movieMap!!["released"])
saveValues.put(FilmContract.SaveEntry.SAVE_FLAG, flag)


val selection = (FilmContract.SaveEntry.TABLE_NAME +
"." + FilmContract.SaveEntry.SAVE_ID + " = ? ")
val selectionArgs = arrayOf(movie_id)

 //  boolean deletePermission = false;
            val alreadyCursor = context.contentResolver.query(FilmContract.SaveEntry.CONTENT_URI, null, selection, selectionArgs, null)

var found = false
while (alreadyCursor!!.moveToNext())
{
 //Already present in databse

                val flag_index = alreadyCursor!!.getColumnIndex(FilmContract.SaveEntry.SAVE_FLAG)
val flag_got = alreadyCursor!!.getInt(flag_index)

if (flag_got == flag)
found = true
}

if (!found)
addToDatabase(saveValues)
else
CustomToast.show(context, "Already present.", false)
}
}

private fun addToDatabase(saveValues:ContentValues) {

val returnedCursor = context.contentResolver.query(FilmContract.SaveEntry.CONTENT_URI, null, null, null, null)
if (returnedCursor!!.moveToFirst() && returnedCursor!!.count == 30)
{
 //No space to fill more. Have to delete oldest entry to save this Agree?

            val alertDialog = AlertDialog.Builder(context)
alertDialog.setTitle("Remove")
alertDialog.setIcon(R.drawable.ic_delete_sweep_black_24dp)

val input = TextView(context)
val container = FrameLayout(context)
val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
params.setMargins(96, 48, 96, 48)
    input.layoutParams = params

    input.text = "Save Limit reached , want to remove the oldest movie and save this one ?"
input.setTextColor(Color.parseColor("#303030"))

container.addView(input)


alertDialog.setView(container)
alertDialog.setPositiveButton("Okay"
) { dialog, which ->
    val deleteSelection = FilmContract.SaveEntry.TABLE_NAME + "." + FilmContract.SaveEntry._ID + " = ? "

    returnedCursor!!.moveToFirst()

    //Log.d(LOG_TAG, "This is the last index value which is going to be deleted "+returnedCursor.getInt(0));

    val deletionArgs = arrayOf((returnedCursor!!.getInt(0)).toString())


    val deletion_id = context.contentResolver.delete(FilmContract.SaveEntry.CONTENT_URI, deleteSelection, deletionArgs).toLong()

    if (!deletion_id.equals(-1)) {

        // Log.d(LOG_TAG, "We deleted this row" + deletion_id);

        val uri = context.contentResolver.insert(FilmContract.SaveEntry.CONTENT_URI, saveValues)

        val movieRowId = ContentUris.parseId(uri)

        if (!movieRowId.equals(-1)) {
            //inserted
            CustomToast.show(context, "Movie saved successfully.", false)

        } else {

            // Log.d(LOG_TAG, "row not Inserted in database");
        }

    } else {

        //delete was unsuccessful
    }

    dialog.cancel()
}

    alertDialog.setNegativeButton("Cancel",
object:DialogInterface.OnClickListener {
override fun onClick(dialog:DialogInterface, which:Int) {
 // Write your code here to execute after dialog
                            dialog.cancel()
}
})

alertDialog.show()
}
else
{

val uri = context.contentResolver.insert(FilmContract.SaveEntry.CONTENT_URI, saveValues)
val movieRowId = ContentUris.parseId(uri)

if (!movieRowId.equals(-1))
CustomToast.show(context, "Movie saved successfully.", false)
else
CustomToast.show(context, "Failed to save movie.", false)

}
}

}
