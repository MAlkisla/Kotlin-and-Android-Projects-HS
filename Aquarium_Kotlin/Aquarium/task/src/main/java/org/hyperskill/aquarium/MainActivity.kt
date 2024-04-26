package org.hyperskill.aquarium

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso

data class AnimalResource(val extrasKey: String, val viewId: Int)

class MainActivity : AppCompatActivity() {
    private val image = AnimalResource("imageAnimals", R.id.image_view)
    private val name = AnimalResource("nameAnimals", R.id.tv_name)
    private val descr = AnimalResource("descriptionAnimals", R.id.tv_description)

    private val defaultAnimalImageList = listOf(
        "https://ucarecdn.com/42045846-b968-4a88-81ec-df73bec4fcb7/",
        "https://ucarecdn.com/5aa10eb3-fc49-4304-9057-adf1d29a9b4c/",
        "https://ucarecdn.com/c5fd39b9-7690-4616-b7dc-d3f8da883146/"
    )

    private val defaultAnimalNameList = listOf("Koi Carp", "Spiny Dogfish", "Kaluga")

    private val defaultAnimalDescriptionList = listOf(
        //Koi Carp
        "These colorful, ornamental fish are a variety of the Amur carp. " +
                "They were originally found in Central Europe and Asia, " +
                "but they’ve spread to many other parts of the world. " +
                "Koi carp are popular with breeders, and there are currently over 100 varieties " +
                "created through breeding.\n" +
                "\n" +
                "The average age of a koi carp can vary based on the part of the world it’s bred in. " +
                "Carps bred outside of Japan have an average lifespan of around 15 years," +
                " while carps bred in Japan can live 40 years or more. The oldest koi carp on record," +
                " which was a fish named Hanako, reportedly lived for 226 years!",

        // Spiny dogfish
        "The spiny dogfish is a type of shark with venomous spines in front of its dorsal fins." +
                " Not only is it an aggressive hunter, but these fish are known to hunt in packs!" +
                " Like many shark species, these fish grow slowly, " +
                "and some females don’t reach full maturity until they’re over 30 years old.\n" +
                "\n" +
                "While the lifespan of the spiny dogfish is already impressive, " +
                "some fish live for far longer than average. " +
                "Spiny dogfish in the Pacific Ocean tend to live longer than fish in the Atlantic," +
                " with some fish living longer than 80 years. " +
                "Females tend to mature later than males, and they usually live longer too.",

        //Kaluga
        "Sometimes called the river beluga, the kaluga is a type of predatory sturgeon." +
                " While these fish spend the majority of their time in freshwater, " +
                "they’re also able to survive in salt water. " +
                "The kaluga is one of the world’s largest freshwater fish species and can grow to be more than 18 feet long, " +
                "with a weight of over 2,200 pounds.\n" +
                "\n" +
                "Kalugas are overfished, which has left the species vulnerable to extinction. " +
                "Although many kaluga are killed before they fully mature, " +
                "these fish have the potential to live very long lives. " +
                "One kaluga that was caught in China is estimated to be over 100 years old."
    )

    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val animalImageIds: List<String> =
            intent.getStringArrayListExtra(image.extrasKey) ?: defaultAnimalImageList
        val animalNames: List<String> =
            intent.getStringArrayListExtra(name.extrasKey) ?: defaultAnimalNameList
        val animalDescriptions: List<String> =
            intent.getStringArrayListExtra(descr.extrasKey) ?: defaultAnimalDescriptionList

        viewPager = findViewById(R.id.viewpager2)

        val adapter = AnimalPagerAdapter(
            animalImageIds,
            animalNames,
            animalDescriptions
        )
        viewPager.adapter = adapter

        // Configure TabLayout with ViewPager2
        TabLayoutMediator(findViewById(R.id.tab_layout), viewPager) { tab, position ->
            tab.text = animalNames[position]
        }.attach()
    }
}

class AnimalPagerAdapter(
    private val images: List<String>,
    private val names: List<String>,
    private val descriptions: List<String>
) : RecyclerView.Adapter<AnimalViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.page_item, parent, false)
        return AnimalViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        Picasso.get()
            .load(images[position])
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error)
            .into(holder.imageView)

        holder.nameTextView.text = names[position]
        holder.descTextView.text = descriptions[position]
    }

    override fun getItemCount(): Int = names.size
}

class AnimalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageView: ImageView = itemView.findViewById(R.id.image_view)
    val nameTextView: TextView = itemView.findViewById(R.id.tv_name)
    val descTextView: TextView = itemView.findViewById(R.id.tv_description)
}
