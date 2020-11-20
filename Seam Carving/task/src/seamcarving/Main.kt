package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.pow
import kotlin.math.sqrt

fun main(args: Array<String>) {
    if (args.size == 4 && args[0] == "-in" && args[2] == "-out") {
        val image = ImageIO.read(File(args[1]))
    
        val seamImage = createHorizontalSeamImage(image)
        
        saveImage(seamImage, args[3])
    } else if (args.size == 8 && args[0] == "-in" && args[2] == "-out" &&
            args[4] == "-width" && args[6] == "-height") {
        val image = ImageIO.read(File(args[1]))
        var reducedImage: BufferedImage = image
        
        for (i in 1..args[5].toInt()) {
            reducedImage = reducedImage.removeSeam(true)
        }
        
        for (i in 1..args[7].toInt()) {
            reducedImage = reducedImage.removeSeam(false)
        }
    
        saveImage(reducedImage, args[3])
    }
}

fun createImage(width: Int, height: Int): BufferedImage {
    val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val graphics = bufferedImage.graphics
    
    graphics.color = Color.RED
    graphics.drawLine(0, 0, width - 1, height - 1)
    graphics.drawLine(width - 1, 0, 0, height - 1)
    
    return bufferedImage
}

fun createNegativeImage(image: BufferedImage): BufferedImage {
    val outputImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
    
    for (i in 0 until image.width) {
        for (j in 0 until image.height) {
            val originalColor = Color(image.getRGB(i, j))
            outputImage.setRGB(i, j,
                    Color(255 - originalColor.red,
                            255 - originalColor.green,
                            255 - originalColor.blue).rgb)
        }
    }
    
    return outputImage
}

fun createEnergyImage(image: BufferedImage): BufferedImage {
    val outputImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
    val energyList = calculateEnergies(image)
    
    val maxEnergy = energyList.maxOf { row -> row.maxOf { it } }
    
    for (i in 0 until outputImage.height) {
        for (j in 0 until outputImage.width) {
            val intensity = (255.0 * energyList[i][j] / maxEnergy).toInt()
            outputImage.setRGB(j, i, Color(intensity, intensity, intensity).rgb)
        }
    }
    
    return outputImage
}

private fun calculateEnergies(image: BufferedImage): MutableList<MutableList<Double>> {
    val energyList = mutableListOf<MutableList<Double>>()
    
    for (y in 0 until image.height) {
        val line = mutableListOf<Double>()
        for (x in 0 until image.width) {
            val calculateY = when (y) {
                0 -> 1
                image.height - 1 -> image.height - 2
                else -> y
            }
            
            val calculateX = when (x) {
                0 -> 1
                image.width - 1 -> image.width - 2
                else -> x
            }
    
            line.add(sqrt(calculateEnergyX(image, calculateX, y) +
                    calculateEnergyY(image, x, calculateY)))
        }
        energyList.add(line)
    }
    
    return energyList
}

fun calculateEnergyX(image: BufferedImage, x: Int, y: Int): Double {
    val leftColor = Color(image.getRGB(x - 1, y))
    val rightColor = Color(image.getRGB(x + 1, y))
    return (leftColor.red - rightColor.red).toDouble().pow(2.0) +
            (leftColor.green - rightColor.green).toDouble().pow(2.0) +
            (leftColor.blue - rightColor.blue).toDouble().pow(2.0)
}

fun calculateEnergyY(image: BufferedImage, x: Int, y: Int): Double {
    val upColor = Color(image.getRGB(x, y - 1))
    val downColor = Color(image.getRGB(x, y + 1))
    return (upColor.red - downColor.red).toDouble().pow(2.0) +
            (upColor.green - downColor.green).toDouble().pow(2.0) +
            (upColor.blue - downColor.blue).toDouble().pow(2.0)
}

fun createVerticalSeamImage(image: BufferedImage): BufferedImage {
    val energyList = calculateEnergies(image)
    
    val seamPixels = findSeamPixels(energyList)
    
    for (y in 0 until image.height) {
        image.setRGB(seamPixels[y], y, Color(255, 0, 0).rgb)
    }
    
    return image
}

fun createHorizontalSeamImage(image: BufferedImage): BufferedImage {
    val energyList = calculateEnergies(image).transpose()
    
    val seamPixels = findSeamPixels(energyList)
    
    for (x in 0 until image.width) {
        image.setRGB(x, seamPixels[x], Color(255, 0, 0).rgb)
    }
    
    return image
}

fun MutableList<MutableList<Double>>.transpose(): MutableList<MutableList<Double>> {
    val resultMatrix = mutableListOf<MutableList<Double>>()
    
    for (i in this[0].indices) {
        val row = mutableListOf<Double>()
        for (j in this.indices) {
            row.add(this[j][i])
        }
        resultMatrix.add(row)
    }
    
    return resultMatrix
}

fun findSeamPixels(energyList: MutableList<MutableList<Double>>): List<Int> {
    val seamPixels = mutableListOf<Int>()
    val smallestEnergyList = mutableListOf<MutableList<Pair<Double, Int>>>()
    smallestEnergyList.add(energyList[0].map { Pair(it, -1) }.toMutableList())
    val maxDoubleList = mutableListOf<Pair<Double, Int>>()
    for (i in 0 until energyList[0].size) maxDoubleList.add(Pair(Double.MAX_VALUE, -1))
    
    for (x in 0 until energyList.size - 1) {
        smallestEnergyList.add(maxDoubleList.toMutableList())

        for (y in 0 until energyList[0].size) {
            if (y != 0) {
                val pendingValueLeft = smallestEnergyList[x][y].first + energyList[x + 1][y - 1]
                if (smallestEnergyList[x + 1][y - 1].first >= pendingValueLeft) {
                    smallestEnergyList[x + 1][y - 1] = Pair(pendingValueLeft, y)
                }
            }

            val pendingValueBottom = smallestEnergyList[x][y].first + energyList[x + 1][y]
            if (smallestEnergyList[x + 1][y].first >= pendingValueBottom) {
                smallestEnergyList[x + 1][y] = Pair(pendingValueBottom, y)
            }

            if (y != energyList[0].size - 1) {
                val pendingValueRight = smallestEnergyList[x][y].first + energyList[x + 1][y + 1]
                if (smallestEnergyList[x + 1][y + 1].first >= pendingValueRight) {
                    smallestEnergyList[x + 1][y + 1] = Pair(pendingValueRight, y)
                }
            }
        }
    }

    var minIndex = -1
    var minValue = Double.MAX_VALUE
    for (y in smallestEnergyList[0].indices) {
        if (smallestEnergyList[energyList.size - 1][y].first < minValue) {
            minIndex = y
            minValue = smallestEnergyList[energyList.size - 1][y].first
        }
    }

    for (x in energyList.size - 1 downTo 1) {
        seamPixels.add(minIndex)
        minIndex = smallestEnergyList[x][minIndex].second
    }

    seamPixels.add(minIndex)
    seamPixels.reverse()

    return seamPixels
}

fun BufferedImage.removeSeam(isVertical: Boolean): BufferedImage {
    val outputWidth = if (isVertical) width - 1 else width
    val outputHeight = if (isVertical) height else height - 1
    
    val outputImage = BufferedImage(outputWidth, outputHeight, BufferedImage.TYPE_INT_RGB)
    
    if (isVertical) {
        val seamImage = createVerticalSeamImage(this)
        for (y in 0 until height) {
            var outputX = 0
            for (x in 0 until width) {
                val color = Color(seamImage.getRGB(x, y))
                if (!(color.red == 255 && color.green == 0 && color.blue == 0)) {
                    outputImage.setRGB(outputX, y, color.rgb)
                    outputX++
                }
            }
        }
    } else {
        val seamImage = createHorizontalSeamImage(this)
        for (x in 0 until width) {
            var outputY = 0
            for (y in 0 until height) {
                val color = Color(seamImage.getRGB(x, y))
                if (!(color.red == 255 && color.green == 0 && color.blue == 0)) {
                    outputImage.setRGB(x, outputY, color.rgb)
                    outputY++
                }
            }
        }
    }
    
    return outputImage
}

fun saveImage(image: BufferedImage, fileName: String) {
    val bos = ByteArrayOutputStream()
    ImageIO.write(image, fileName.split(".")[1], bos)
    
    val data = bos.toByteArray()
    
    val bis = ByteArrayInputStream(data)
    val outputImage = ImageIO.read(bis)
    
    ImageIO.write(outputImage, fileName.split(".")[1], File(fileName))
}
