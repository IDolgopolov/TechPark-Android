package com.pascal.homework

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NumbersListFragment : Fragment(), View.OnClickListener {
    private var numbersCount: Int = 100
    private var APP_PREFERENCES_COUNTER: String = "prefNumberCount"
    private var APP_INSTANCE_COUNTER: String = "instNumberCount"
    private lateinit var mRvNumbers: RecyclerView
    private lateinit var mAdapter: NumbersAdapter
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        prefs = activity!!.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return inflater.inflate(R.layout.fragment_numbers_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        numbersCount = savedInstanceState?.getInt(APP_INSTANCE_COUNTER) ?: 100

        mRvNumbers = view.findViewById(R.id.numbers_list)
        mAdapter = NumbersAdapter()

        mRvNumbers.adapter = mAdapter
        mRvNumbers.layoutManager = GridLayoutManager(
            context,
            when (resources.configuration.orientation) {
                Configuration.ORIENTATION_PORTRAIT -> 3
                else -> 4
            }
        )
        view.findViewById<Button>(R.id.add_number_btn).setOnClickListener(this)
    }

    override fun onPause() {
        super.onPause()
        val editor = prefs.edit()
        editor.putInt(APP_PREFERENCES_COUNTER, numbersCount).apply()
    }

    override fun onResume() {
        super.onResume()
        if (prefs.contains(APP_PREFERENCES_COUNTER)) {
            var counter = prefs.getInt(APP_PREFERENCES_COUNTER, 0)
            numbersCount = counter
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(APP_INSTANCE_COUNTER, numbersCount)
    }

    override fun onClick(view: View?) {
        if (view?.id == R.id.add_number_btn) {
            mAdapter.addNumber()
            mRvNumbers.smoothScrollToPosition(numbersCount - 1)
        } else if (view?.findViewById<TextView>(R.id.number_text) !== null) {
            val txtView = view.findViewById<TextView>(R.id.number_text)

            val bundle = Bundle()
            bundle.putInt("number", txtView.text.toString().toInt())

            val numberFragment = NumberFragment()
            numberFragment.arguments = bundle

            activity!!.supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_frame, numberFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    // Adapter
    private inner class NumbersAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val layoutInflater = LayoutInflater.from(parent?.context)
            val numberView = layoutInflater.inflate(R.layout.cell_number, parent, false)
            return NumbersViewHolder(itemView = numberView)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var mTxtNumber: TextView = holder.itemView.findViewById(R.id.number_text)

            var curNumber: Int = position + 1
            mTxtNumber.text = (curNumber).toString()
            mTxtNumber.setTextColor(
                holder.itemView.resources.getColor(
                    when {
                        curNumber % 2 == 1 -> R.color.blue
                        else -> R.color.red
                    }
                )
            )
            holder.itemView.setOnClickListener(this@NumbersListFragment)
        }

        override fun getItemCount(): Int {
            return numbersCount
        }

        fun addNumber() {
            numbersCount++
            notifyItemInserted(numbersCount - 1)
        }

        inner class NumbersViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {}
    }
}
