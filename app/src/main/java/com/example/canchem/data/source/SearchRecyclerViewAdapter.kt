package com.example.canchem.data.source

import android.view.LayoutInflater
import com.example.canchem.ui.searchHistory.SearchHistoryActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.canchem.databinding.ItemSearchBinding
import android.view.ViewGroup


class SearchRecyclerViewAdapter: RecyclerView.Adapter<SearchRecyclerViewAdapter.MyViewHolder>() {

    var datalist = mutableListOf<SearchData>()//리사이클러뷰에서 사용할 데이터 미리 정의 -> 나중에 MainActivity등에서 datalist에 실제 데이터 추가


    //만들어진 뷰홀더 없을때 뷰홀더(레이아웃) 생성하는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemSearchBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int =datalist.size

    //recyclerview가 viewholder를 가져와 데이터 연결할때 호출
    //적절한 데이터를 가져와서 그 데이터를 사용하여 뷰홀더의 레이아웃 채움
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(datalist[position])
    }

    inner class MyViewHolder(private val binding: ItemSearchBinding): RecyclerView.ViewHolder(binding.root) {

        //private val searchActivity = SearchHistoryActivity.getInstance()
        private var mData: SearchData? = null

        init {
            binding.buttonX.setOnClickListener {
//            Toast.makeText(itemView.context, "눌리긴함", Toast.LENGTH_SHORT).show()
                SearchHistoryActivity.deleteData(mData!!)
//            Toast.makeText(itemView.context, "$SearchHistoryActivity.getInstance()", Toast.LENGTH_SHORT).show()
//            SearchHistoryActivity.getInstance()?.sayHello()
            }
        }



        fun bind(searchData: SearchData){
            mData = searchData
            binding.searchText.text = searchData.name
        }

    }
}


