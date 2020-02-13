package net.vrgsoft.arcprogress

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import kotlin.math.ceil
import kotlin.math.cos

class ArcProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint()
    private val textPaint = TextPaint()
    private val rectF = RectF()
    var thickness = 0f
        set(value) {
            field = value
            initPainters()
        }
    var suffixTextSize = 0f
        set(value) {
            field = value
            initPainters()
        }
    var textSize = 0f
        set(value) {
            field = value
            initPainters()
        }
    private var textColor = 0
        set(value) {
            field = value
            invalidate()
        }
    var progress = 0
        set(value) {
            field = value
            if (field > max) {
                field = max
            }
            initPainters()
        }
    var max = 0
        set(max) {
            if (max > 0) {
                field = max
                invalidate()
            }
        }
    var progressStartStrokeColor = 0
        set(value) {
            field = value
            initPainters()
        }
    var progressEndStrokeColor = 0
        set(value) {
            field = value
            initPainters()
        }
    var unfinishedStrokeColor = 0
        set(value) {
            field = value
            initPainters()
        }
    var arcAngle = 0f
        set(value) {
            field = value
            initPainters()
        }
    var suffixText: String = "%"
        set(value) {
            field = value
            initPainters()
        }
    var suffixTextPadding = 0f
        set(value) {
            field = value
            initPainters()
        }
    private var arcBottomHeight = 0f
    private val defaultProgressColor = Color.GREEN
    private val defaultUnfinishedColor = Color.rgb(72, 106, 176)
    private val defaultTextColor = Color.rgb(66, 145, 241)
    private val defaultSuffixTextSize = spToPx(resources, 15f)
    private val defaultSuffixPadding = dpToPx(resources, 4f)
    private val defaultStrokeWidth = dpToPx(resources, 4f)
    private val defaultSuffixText = "%"
    private val defaultMax = 100
    private val defaultArcAngle = 360 * 0.8f
    private var defaultTextSize = spToPx(resources, 40f)
    private val minSize = dpToPx(resources, 100f)
    private lateinit var colors: IntArray
    private var positions = floatArrayOf(0.2025f, 0.7349f)
    private var gradient: Shader? = null

    init {
        val attributes = context.theme
            .obtainStyledAttributes(attrs, R.styleable.ArcProgressBar, defStyleAttr, 0)
        initByAttributes(attributes)
        attributes.recycle()
        initPainters()
    }

    private fun initByAttributes(attributes: TypedArray) {
        progressStartStrokeColor =
            attributes.getColor(
                R.styleable.ArcProgressBar_arc_progress_start_color,
                defaultProgressColor
            )
        progressEndStrokeColor =
            attributes.getColor(
                R.styleable.ArcProgressBar_arc_progress_end_color,
                progressStartStrokeColor
            )

        colors = intArrayOf(
            progressStartStrokeColor,
            progressEndStrokeColor
        )

        unfinishedStrokeColor = attributes.getColor(
            R.styleable.ArcProgressBar_arc_unfinished_color,
            defaultUnfinishedColor
        )
        textColor = attributes.getColor(R.styleable.ArcProgressBar_arc_text_color, defaultTextColor)
        textSize =
            attributes.getDimension(R.styleable.ArcProgressBar_arc_text_size, defaultTextSize)
        arcAngle = attributes.getFloat(R.styleable.ArcProgressBar_arc_angle, defaultArcAngle)
        max = attributes.getInt(R.styleable.ArcProgressBar_arc_max, defaultMax)
        progress = attributes.getInt(R.styleable.ArcProgressBar_arc_progress, 0)
        thickness =
            attributes.getDimension(
                R.styleable.ArcProgressBar_arc_thickness,
                defaultStrokeWidth.toFloat()
            )
        suffixTextSize = attributes.getDimension(
            R.styleable.ArcProgressBar_arc_suffix_text_size,
            defaultSuffixTextSize
        )
        suffixText =
            if (attributes.getString(R.styleable.ArcProgressBar_arc_suffix_text).isNullOrEmpty())
                defaultSuffixText
            else
                attributes.getString(R.styleable.ArcProgressBar_arc_suffix_text).orEmpty()
        suffixTextPadding = attributes.getDimension(
            R.styleable.ArcProgressBar_arc_suffix_text_padding,
            defaultSuffixPadding.toFloat()
        )
    }

    private fun initPainters() {
        textPaint.apply {
            color = textColor
            textSize = textSize
            isAntiAlias = true
        }
        paint.apply {
            color = defaultUnfinishedColor
            isAntiAlias = true
            strokeWidth = thickness
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }
        invalidate()
    }

    override fun getSuggestedMinimumHeight(): Int = minSize

    override fun getSuggestedMinimumWidth(): Int = minSize

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        rectF[thickness / 2f, thickness / 2f, width - thickness / 2f] =
            MeasureSpec.getSize(heightMeasureSpec) - thickness / 2f
        val radius = width / 2f
        val angle = (360 - arcAngle) / 2f
        arcBottomHeight = radius * (1 - cos(angle / 180 * Math.PI)).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val startAngle = 270 - arcAngle / 2f
        val finishedSweepAngle = progress / max.toFloat() * arcAngle
        var finishedStartAngle = startAngle
        if (progress == 0) finishedStartAngle = 0.01f
        paint.color = unfinishedStrokeColor
        canvas.drawArc(rectF, startAngle, arcAngle, false, paint)
        paint.color = progressStartStrokeColor
        paint.shader = gradient
        canvas.drawArc(rectF, finishedStartAngle, finishedSweepAngle, false, paint)
        paint.shader = null
        val text = progress.toString()
        if (!TextUtils.isEmpty(text)) {
            textPaint.color = textColor
            textPaint.textSize = textSize
            val textHeight = textPaint.descent() + textPaint.ascent()
            val textBaseline =
                height - arcBottomHeight - (textPaint.descent() + textPaint.ascent()) / 2
            canvas.drawText(
                text,
                (width - textPaint.measureText(text)) / 2.0f,
                textBaseline,
                textPaint
            )

            textPaint.textSize = suffixTextSize
            val suffixHeight = textPaint.descent() + textPaint.ascent()
            canvas.drawText(
                suffixText,
                width / 2.0f + textPaint.measureText(text) + suffixTextPadding,
                textBaseline + textHeight - suffixHeight,
                textPaint
            )
        }
        if (arcBottomHeight == 0f) {
            val radius = width / 2f
            val angle = (360 - arcAngle) / 2f
            arcBottomHeight =
                radius * (1 - cos(angle / 180 * Math.PI)).toFloat()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val gradientMatrix = Matrix()
        gradientMatrix.preRotate(ROTATE_DEGREES, w / 2f, h / 2f)
        gradient = LinearGradient(
            0f,
            0f,
            w.toFloat(),
            h.toFloat(),
            colors,
            positions,
            Shader.TileMode.REPEAT
        ).apply {
            setLocalMatrix(gradientMatrix)
        }
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onSaveInstanceState(): Parcelable? {
        return Bundle().apply {
            putParcelable(INSTANCE_STATE, super.onSaveInstanceState())
            putInt(INSTANCE_PROGRESS, progress)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            progress = state.getInt(INSTANCE_PROGRESS)
            initPainters()
            super.onRestoreInstanceState(state.getParcelable(INSTANCE_STATE))
            return
        }
        super.onRestoreInstanceState(state)
    }

    companion object {
        private const val INSTANCE_STATE = "saved_instance"
        private const val INSTANCE_PROGRESS = "progress"
        private const val ROTATE_DEGREES = 100f

        @JvmStatic
        private fun dpToPx(resources: Resources, dp: Float): Int {
            val scale: Float = resources.displayMetrics.density
            return ceil(dp * scale).toInt()
        }

        @JvmStatic
        private fun spToPx(resources: Resources, sp: Float): Float {
            val scale: Float = resources.displayMetrics.scaledDensity
            return sp * scale
        }
    }
}