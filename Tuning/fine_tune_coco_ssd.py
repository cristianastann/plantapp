import tensorflow as tf
import glob

# Constants
BATCH_SIZE = 32
NUM_CLASSES = 1081

# Helper function to load datasets
def load_dataset(path):
    image_files = glob.glob(path + "\*.jpg")
    
    # Convert the list of image paths to a tf.data.Dataset
    dataset = tf.data.Dataset.from_tensor_slices(image_files)
    dataset = dataset.map(load_and_preprocess)
    
    # Shuffle, batch, and prefetch
    dataset = dataset.shuffle(buffer_size=len(image_files))
    dataset = dataset.batch(BATCH_SIZE)
    dataset = dataset.prefetch(buffer_size=tf.data.experimental.AUTOTUNE)
    
    return dataset

# Load datasets
train_dataset = load_dataset("D:\Dissertation\Pl@ntNet_300K\plantnet_300K\images_train")
val_dataset = load_dataset("D:\Dissertation\Pl@ntNet_300K\plantnet_300K\images_val")
test_dataset = load_dataset("D:\Dissertation\Pl@ntNet_300K\plantnet_300K\images_test")


# Load MobileNetV2 and build the model
base_model = tf.keras.applications.MobileNetV2(input_shape=(300, 300, 3), include_top=False, weights='imagenet')
base_model.trainable = False

model = tf.keras.Sequential([
    base_model,
    tf.keras.layers.GlobalAveragePooling2D(),
    tf.keras.layers.Dense(1081, activation='relu'),
    tf.keras.layers.Dropout(0.5),
    tf.keras.layers.Dense(NUM_CLASSES, activation='softmax')])

# Compile the model
model.compile(optimizer=tf.keras.optimizers.Adam(), loss='categorical_crossentropy', metrics=['accuracy'])

# Train the model
history = model.fit(train_dataset, epochs=10, validation_data=val_dataset)
