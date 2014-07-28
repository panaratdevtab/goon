package com.tmstudio.goon;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.R.integer;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tmstudio.goon.R;

import com.tmstudio.goon.R;
import com.tmstudio.goon.services.UserSessionManager;

@SuppressLint("NewApi")
public class CalendarView extends Activity implements OnClickListener {

	// params for progress bar
	LinearLayout progress;
	LinearLayout frame;
	LinearLayout showProgressLinearLayout;
	RelativeLayout PinPosition;
	int maxwidth;

	TextView showProgress;
	TextView wombAge;
	LinearLayout FrameShowProgress;
	LinearLayout FrameFullProgress;
	LinearLayout todayWeightLayout;

	TextView showDate;
	TextView fullday;

	// params for calendar
	private TextView currentMonth;
	private TextView currentYear;
	private ImageView prevMonth;
	private ImageView nextMonth;
	private GridView calendarView;
	private GridCellAdapter adapter;
	private Calendar _calendar;
	private int month, year;
	private static final String monthTemplate = "MMMM";
	private static final String yearTemplate = "yyyy";
	String flag = "abc";
	String date_month_year;
	int point;
	int pos;
	
	Spinner chooseMonthSpinner;

	// params for date picker
	static final int DATE_DIALOG_ID = 1;
	private int mYear;
	private int mMonth;
	private int mDay;
	private EditText etPickADate;
	
	// User Session Manager Class
	UserSessionManager sessionlogin;
	private String pref_fid, pref_uid, pref_email, pref_password, pref_child_name, pref_child_age, pref_avatar, pref_dad_name, pref_mom_name, pref_child_sex, pref_born_date, pref_status, pref_status_login, pref_contry;	
	
		
	// BackPress
	private long backPressed = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar_view);
		
		// Session class instance
		sessionlogin = new UserSessionManager(getApplicationContext());	
		getSession();
	
		// set progress bar
				// get resolution width and height
						Display display = getWindowManager().getDefaultDisplay();
						Point size = new Point();
						display.getSize(size);
						int maxwidth = size.x;

						// /// Progress event
								showProgress = (TextView) findViewById(R.id.showProgress1);
								TextView fullday = (TextView) findViewById(R.id.fulldays1);
								TextView wombAge = (TextView) findViewById(R.id.wombage1);

								frame = (LinearLayout) findViewById(R.id.frame1);
								progress = (LinearLayout) findViewById(R.id.progressbar);
								
								// convert 60 dp to px
								Resources r1 = getResources();
								float layoutPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, r1.getDisplayMetrics());
								
										int width =  Math.round(layoutPx) * 2;
								
								        int frameWidth = maxwidth - width;
								        
								        int currentDay = Integer.parseInt(pref_child_age);

										float currentProgressValue = (currentDay * frameWidth) / 280;
										
										frame.setLayoutParams(new LinearLayout.LayoutParams(Math.round(frameWidth), LinearLayout.LayoutParams.MATCH_PARENT));
										LinearLayout.LayoutParams frameParams = (LinearLayout.LayoutParams) frame.getLayoutParams();
										//frameParams.setMargins(0, 16, 0, 0);
										frame.setLayoutParams(frameParams);
										
										progress.setLayoutParams(new LinearLayout.LayoutParams(Math.round(currentProgressValue),LinearLayout.LayoutParams.MATCH_PARENT));

										showProgress.setText(Math.round(currentDay) + "\n" + getResources().getString(R.string.day));
										fullday.setText("280\n" + getResources().getString(R.string.day));
										wombAge.setText("●  "+ getResources().getString(R.string.womb_age) + " " + Math.round(currentDay) + " " + getResources().getString(R.string.day));

										RelativeLayout PinPosition = (RelativeLayout) findViewById(R.id.pinpos1);
										
										// position of red pin (mark Today)
										RelativeLayout.LayoutParams paramsOfToday = (RelativeLayout.LayoutParams) PinPosition.getLayoutParams();
										
										// convert 12 dp to px
										Resources r2 = getResources();
										float pinPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, r2.getDisplayMetrics());
										
										paramsOfToday.setMargins(Math.round((layoutPx - pinPx) + currentProgressValue), 0, Math.round(pinPx), 0);
										PinPosition.setLayoutParams(paramsOfToday);

				//////////////////////////////////////////////////////////////////
				///// Calendar event
				_calendar = Calendar.getInstance(Locale.getDefault());
				month = _calendar.get(Calendar.MONTH) + 1;
				year = _calendar.get(Calendar.YEAR);

				prevMonth = (ImageView) this.findViewById(R.id.prevMonth);
				prevMonth.setOnClickListener(this);

				currentMonth = (TextView) this.findViewById(R.id.monthchoosed);
				currentMonth.setText(DateFormat.format(monthTemplate,
						_calendar.getTime()));
				
				currentMonth.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						final Calendar c = Calendar.getInstance();
						mYear = year;
						mMonth = month;
						customDatePicker();
						
						
					}
				});

				nextMonth = (ImageView) this.findViewById(R.id.nextMonth);
				nextMonth.setOnClickListener(this);

				calendarView = (GridView) this.findViewById(R.id.calendar);

				// Initialised
				adapter = new GridCellAdapter(getApplicationContext(),
						R.id.calendar_day_gridcell, month, year);
				adapter.notifyDataSetChanged();
				calendarView.setAdapter(adapter);

				// set line imageview
				ImageView line = (ImageView) findViewById(R.id.line);
				line.setScaleType(ScaleType.FIT_XY);
				
			}
	
			public void getSession() {
				// get user data from session
				HashMap<String, String> user = sessionlogin.getUserDetails();
				// get name
				pref_uid = user.get(UserSessionManager.KEY_UID);
				pref_fid = user.get(UserSessionManager.KEY_FID);
				pref_email = user.get(UserSessionManager.KEY_EMAIL);
				pref_password = user.get(UserSessionManager.KEY_PASSWORD);
				pref_child_name = user.get(UserSessionManager.KEY_CHILD_NAME);
				pref_child_age = user.get(UserSessionManager.KEY_CHILD_AGE);
				pref_avatar = user.get(UserSessionManager.KEY_AVATAR);
				pref_dad_name = user.get(UserSessionManager.KEY_DAD_NAME);
				pref_mom_name = user.get(UserSessionManager.KEY_MOM_NAME);
				pref_child_sex = user.get(UserSessionManager.KEY_CHILD_SEX);
				pref_born_date = user.get(UserSessionManager.KEY_BORN_DATE);
				pref_status = user.get(UserSessionManager.KEY_STATUS);
				pref_status_login = user.get(UserSessionManager.KEY_STATUS_LOGIN);
				pref_contry = user.get(UserSessionManager.KEY_CONTRY);
			}

			private void setGridCellAdapterToDate(int month, int year) {
				adapter = new GridCellAdapter(getApplicationContext(),
						R.id.calendar_day_gridcell, month, year);
				_calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
				currentMonth.setText(DateFormat.format(monthTemplate,
						_calendar.getTime()));
				adapter.notifyDataSetChanged();
				calendarView.setAdapter(adapter);
			}

			@Override
			public void onClick(View v) {
				if (v == prevMonth) {
					if (month <= 1) {
						month = 12;
						year--;
					} else
						month--;
					setGridCellAdapterToDate(month, year);
				}
				if (v == nextMonth) {
					if (month > 11) {
						month = 1;
						year++;
					} else
						month++;
					setGridCellAdapterToDate(month, year);
				}

			}

			// ///////////////////////////////////////////////////////////////////////////////////////
			// Inner Class
			public class GridCellAdapter extends BaseAdapter implements OnClickListener {
				private final Context _context;

				private final List<String> list;
				private static final int DAY_OFFSET = 1;
				private final String[] months = { "January", "February", "March",
						"April", "May", "June", "July", "August", "September",
						"October", "November", "December" };
				private final int[] daysOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30,
						31, 30, 31 };
				private int daysInMonth;
				private int currentDayOfMonth;
				private int currentWeekDay;
				private Button gridcell;
				private TextView num_events_per_day;
				private final HashMap<String, Integer> eventsPerMonthMap;

				// Days in Current Month
				public GridCellAdapter(Context context, int textViewResourceId,
						int month, int year) {
					super();
					this._context = context;
					this.list = new ArrayList<String>();
					Calendar calendar = Calendar.getInstance();
					setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
					setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));

					// Print Month
					printMonth(month, year);

					// Find Number of Events
					eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
				}

				private String getMonthAsString(int i) {
					return months[i];
				}

				private int getNumberOfDaysOfMonth(int i) {
					return daysOfMonth[i];
				}

				public String getItem(int position) {
					return list.get(position);
				}

				@Override
				public int getCount() {
					return list.size();
				}

				private void printMonth(int mm, int yy) {
					int trailingSpaces = 0;
					int daysInPrevMonth = 0;
					int prevMonth = 0;
					int prevYear = 0;
					int nextMonth = 0;
					int nextYear = 0;

					int currentMonth = mm - 1;
					daysInMonth = getNumberOfDaysOfMonth(currentMonth);

					// Gregorian Calendar : MINUS 1, set to FIRST OF MONTH
					GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);

					if (currentMonth == 11) {
						prevMonth = currentMonth - 1;
						daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
						nextMonth = 0;
						prevYear = yy;
						nextYear = yy + 1;
					} else if (currentMonth == 0) {
						prevMonth = 11;
						prevYear = yy - 1;
						nextYear = yy;
						daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
						nextMonth = 1;
					} else {
						prevMonth = currentMonth - 1;
						nextMonth = currentMonth + 1;
						nextYear = yy;
						prevYear = yy;
						daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
					}

					int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
					trailingSpaces = currentWeekDay;

					if (cal.isLeapYear(cal.get(Calendar.YEAR)) && mm == 1) {
						++daysInMonth;
					}

					// Trailing Month days
					for (int i = 0; i < trailingSpaces; i++) {
						list.add(String
								.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET)
										+ i)
								+ "-GREY"
								+ "-"
								+ getMonthAsString(prevMonth)
								+ "-"
								+ prevYear);
					}

					// Current Month Days
					for (int i = 1; i <= daysInMonth; i++) {
						if (i == getCurrentDayOfMonth())
							list.add(String.valueOf(i) + "-BLUE" + "-"
									+ getMonthAsString(currentMonth) + "-" + yy);
						else
							list.add(String.valueOf(i) + "-WHITE" + "-"
									+ getMonthAsString(currentMonth) + "-" + yy);
					}

					// Leading Month days
					for (int i = 0; i < list.size() % 7; i++) {
						list.add(String.valueOf(i + 1) + "-GREY" + "-"
								+ getMonthAsString(nextMonth) + "-" + nextYear);
					}
				}

				private HashMap<String, Integer> findNumberOfEventsPerMonth(int year,
						int month) {
					HashMap<String, Integer> map = new HashMap<String, Integer>();
					return map;
				}

				@Override
				public long getItemId(int position) {
					return position;
				}

				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					View row = convertView;
					if (row == null) {
						LayoutInflater inflater = (LayoutInflater) _context
								.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						row = inflater.inflate(R.layout.calendar_day_gridcell, parent,
								false);
					}

					// Get a reference to the Day gridcell
					gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
					gridcell.setOnClickListener(this);

					// ACCOUNT FOR SPACING

					String[] day_color = list.get(position).split("-");
					String theday = day_color[0];
					String themonth = day_color[2];
					String theyear = day_color[3];
					if ((!eventsPerMonthMap.isEmpty()) && (eventsPerMonthMap != null)) {
						if (eventsPerMonthMap.containsKey(theday)) {
							num_events_per_day = (TextView) row
									.findViewById(R.id.num_events_per_day);
							Integer numEvents = (Integer) eventsPerMonthMap.get(theday);
							num_events_per_day.setText(numEvents.toString());
						}
					}

					// Set the Day GridCell
					gridcell.setText(theday);
					gridcell.setTag(theday + "-" + themonth + "-" + theyear);

					if (day_color[1].equals("GREY"))
						gridcell.setTextColor(Color.LTGRAY);

					if (day_color[1].equals("WHITE"))
						gridcell.setTextColor(Color.BLACK);

					if (day_color[1].equals("BLUE")) {
						gridcell.setTextColor(Color.WHITE);
						gridcell.setBackgroundResource(R.drawable.goon_ipad_calendar_bg_text);
					}
					return row;
				}

				@Override
				public void onClick(View view) {
					// clicked event
					date_month_year = (String) view.getTag();
					int i = 1;
					String[] dateSplit = date_month_year.split("-");
					String mm = dateSplit[1];
					for (String m : months) {
						if (!m.equals(mm)) {
							i++;
						} else {
							break;
						}
					}
					String dMY;
					int iDay = Integer.parseInt(dateSplit[0]);
					if (i > 0 && i < 10 && iDay > 0 && iDay < 10) {
						dMY = dateSplit[2] + "/0" + i + "/0" + dateSplit[0];
					} else if (i > 0 && i < 10) {
						dMY = dateSplit[2] + "/0" + i + "/" + dateSplit[0];
					} else {
						dMY = dateSplit[2] + "/" + i + "/" + dateSplit[0];
					}
					Intent intent = new Intent(CalendarView.this, Diary.class);
					intent.putExtra("date", dMY);
					Toast.makeText(getApplicationContext(), dMY +"", Toast.LENGTH_SHORT).show();
					startActivity(intent);
				}

				public int getCurrentDayOfMonth() {
					return currentDayOfMonth;
				}

				private void setCurrentDayOfMonth(int currentDayOfMonth) {
					this.currentDayOfMonth = currentDayOfMonth;
				}

				public void setCurrentWeekDay(int currentWeekDay) {
					this.currentWeekDay = currentWeekDay;
				}

				public int getCurrentWeekDay() {
					return currentWeekDay;
				}
			}
			
			///// Date picker dialog
			DatePickerDialog.OnDateSetListener mDateSetListner = new OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int _year, int _month,
						int _day) {

					year = _year;
					month = _month+1;
					
					setGridCellAdapterToDate(month, year);
					
				}
			};


			@SuppressWarnings("deprecation")
			protected void updateDate() {
				showDialog(DATE_DIALOG_ID);
			}
			
			/// Inner class for Custonm date picker dialog
			@SuppressLint("SimpleDateFormat")
			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			class CustomDatePickerDialog extends DatePickerDialog implements OnDateChangedListener {

				private DatePickerDialog mDatePicker;

				@SuppressLint("NewApi")
				public CustomDatePickerDialog(Context context, OnDateSetListener callBack,
				        int year, int monthOfYear, int dayOfMonth) {
					
				    super(context, callBack, year, monthOfYear, dayOfMonth);
				    
				    mDatePicker = new DatePickerDialog(context,callBack, year, monthOfYear, dayOfMonth);

				    mDatePicker.getDatePicker().init(year, monthOfYear , dayOfMonth, this);

				}
				public void onDateChanged(DatePicker view, int year,
				        int month, int day) {
				    updateTitle(year, month);
				}
				private void updateTitle(int year, int month) {
					
				    mDatePicker.setTitle("Choose month and year");

				}   

				public DatePickerDialog getPicker(){

				    return this.mDatePicker;
				} 
			}
			
			private void customDatePicker() {
				CustomDatePickerDialog dpd = new CustomDatePickerDialog(this, mDateSetListner,
						mYear, mMonth, mDay);
				
				DatePickerDialog obj = dpd.getPicker();
				obj.setTitle("Choose month and year");
		        try{
		                   Field[] datePickerDialogFields = obj.getClass().getDeclaredFields();
		                   for (Field datePickerDialogField : datePickerDialogFields) { 
		                       if (datePickerDialogField.getName().equals("mDatePicker")) {
		                           datePickerDialogField.setAccessible(true);
		                           DatePicker datePicker = (DatePicker) datePickerDialogField.get(obj);
		                           Field datePickerFields[] = datePickerDialogField.getType().getDeclaredFields();
		                           for (Field datePickerField : datePickerFields) {
		                              if ("mDayPicker".equals(datePickerField.getName()) || "mDaySpinner".equals(datePickerField
		                                .getName())) {
		                                 datePickerField.setAccessible(true);
		                                 Object dayPicker = new Object();
		                                 dayPicker = datePickerField.get(datePicker);
		                                 ((View) dayPicker).setVisibility(View.GONE);
		                              }
		                           }
		                        }

		                     }
		                   }catch(Exception ex){
		                   }
		        obj.show();
			}

			// convert px to dp
			public int pxToDp(int px) {
				DisplayMetrics displayMetrics = getApplicationContext().getResources()
						.getDisplayMetrics();
				int dp = Math.round(px
						/ (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
				return dp;
			}

			// convert dp to px
			public int dpToPx(int dp) {
				DisplayMetrics displayMetrics = getApplicationContext().getResources()
						.getDisplayMetrics();
				int px = Math.round(dp
						* (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
				return px;
			}
			
	@Override
	public void onBackPressed() {
		if (backPressed + 2000 > System.currentTimeMillis()) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
	        intent.addCategory(Intent.CATEGORY_HOME);
	        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        startActivity(intent);
		} else {
			Toast.makeText(getBaseContext(), "Press click again to exit!", Toast.LENGTH_SHORT).show();
		}
		backPressed = System.currentTimeMillis();
	}
}