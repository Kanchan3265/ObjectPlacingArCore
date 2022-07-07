package com.example.arcoreexample

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

class MainActivity : AppCompatActivity() {
    private val MIN_OPENGL_VERSION = 3.0
    private val modelsLiveData = MutableLiveData<Model3D>()
    private var list = arrayListOf<Model3D>()
    lateinit var arFragment: ArFragment
    private var temp =  "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/Box/glTF/Box.gltf"

    @RequiresApi(VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!checkIsSupportedDeviceOrFinish(this)) {
            return
        }
        modelsLiveData.observeForever {
            temp = it.url
        }
        setContentView(R.layout.activity_main)
        list.add(Model3D("Box", "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/Box/glTF/Box.gltf"))
        list.add(Model3D("Duck", "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/Duck/glTF/Duck.gltf"))
        list.add(Model3D("ToyCar", "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/ToyCar/glTF/ToyCar.gltf"))
        list.add(Model3D("WaterBottle", "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/WaterBottle/glTF/WaterBottle.gltf"))
        list.add(Model3D("Sponza", "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/Sponza/glTF/Sponza.gltf"))
        list.add(Model3D("MosquitoInAmber", "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/MosquitoInAmber/glTF/MosquitoInAmber.gltf"))
        list.add(Model3D("Avocado", "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/Avocado/glTF/Avocado.gltf"))
        list.add(Model3D("AnimatedCube", "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/AnimatedCube/glTF/AnimatedCube.gltf"))
        arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment
        val recyclerView = findViewById<RecyclerView>(R.id.rv)
        recyclerView.adapter = ModelsAdapter(list,modelsLiveData)
        recyclerView.layoutManager = GridLayoutManager(this,3)
        arFragment.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane?, motionEvent: MotionEvent? ->
            val anchor: Anchor = hitResult.createAnchor()
            placeObject(arFragment, anchor)
        }
    }

    @RequiresApi(VERSION_CODES.N)
    private fun placeObject(arFragment: ArFragment, anchor: Anchor) {
        ModelRenderable.builder()
            .setSource(this, RenderableSource.builder().setSource(
                this,
                Uri.parse(temp),
                RenderableSource.SourceType.GLTF2)
                .setScale(0.25f)  // Scale the original model to 50%.
                .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                .build())
            .setRegistryId(temp)
            .build()
            .thenAccept{renderable -> addNodeToScene(arFragment,anchor, renderable)
            }
            .exceptionally { throwable: Throwable ->
                Toast.makeText(arFragment.context, "Error:" + throwable.message, Toast.LENGTH_LONG)
                    .show()
                null
            }
    }

    @RequiresApi(VERSION_CODES.N)
    private fun addNodeToScene(arFragment: ArFragment, anchor: Anchor, renderable: Renderable) {
        val blueSphereRenderable:ModelRenderable = renderable.makeCopy() as ModelRenderable
        val anchorNode = AnchorNode(anchor)
        val node = TransformableNode(arFragment.transformationSystem)
        node.renderable = blueSphereRenderable
        node.setParent(anchorNode)
        arFragment.arSceneView.scene.addChild(anchorNode)
        node.select()
    }


    private fun checkIsSupportedDeviceOrFinish(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT < VERSION_CODES.N) {
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show()
            activity.finish()
            return false
        }
        val openGlVersionString =
            (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                .deviceConfigurationInfo
                .glEsVersion
        if (openGlVersionString.toDouble() < MIN_OPENGL_VERSION) {
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG).show()
            activity.finish()
            return false
        }
        return true
    }
}
