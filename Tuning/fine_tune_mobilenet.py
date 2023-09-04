import tensorflow as tf
import glob
from tensorflow.keras.preprocessing import image_dataset_from_directory

# Constants
BATCH_SIZE = 32
NUM_CLASSES = 1081

def load_and_preprocess(image_file):
    image = tf.io.read_file(image_file)
    image = tf.image.decode_jpeg(image, channels=3)
    image = tf.image.resize(image, [300, 300])
    return image

def wrapped_load_and_preprocess(image_file):
    image = tf.py_function(func=load_and_preprocess, inp=[image_file], Tout=tf.float32)
    image.set_shape([300, 300, 3])
    return image


def load_dataset(path):
    # Load the dataset and preprocess it
    dataset = image_dataset_from_directory(
        path,
        shuffle=True,
        batch_size=32,
        image_size=(300, 300),
        label_mode='categorical',
    )
    return dataset


# Load datasets
train_dataset = load_dataset("D:\Dissertation\Pl@ntNet_300K\plantnet_300K\images_train")
val_dataset = load_dataset("D:\Dissertation\Pl@ntNet_300K\plantnet_300K\images_val")
test_dataset = load_dataset("D:\Dissertation\Pl@ntNet_300K\plantnet_300K\images_test")

# Data augmentation
data_augmentation = tf.keras.Sequential([tf.keras.layers.experimental.preprocessing.RandomFlip('horizontal'), tf.keras.layers.experimental.preprocessing.RandomRotation(0.1), tf.keras.layers.experimental.preprocessing.RandomZoom(0.1),])



# Load MobileNetV2 and build the model
base_model = tf.keras.applications.MobileNetV2(input_shape=(300, 300, 3), include_top=False, weights='imagenet')
for layer in base_model.layers[:-50]:
    layer.trainable = False

model = tf.keras.Sequential([data_augmentation,base_model, tf.keras.layers.GlobalAveragePooling2D(), tf.keras.layers.Dense(1081, activation='relu'), tf.keras.layers.Dropout(0.5), tf.keras.layers.Dense(NUM_CLASSES, activation='softmax')])

# Compile the model
optimizer = tf.keras.optimisers.Adam(learning_rate=0.0001)
model.compile(optimiser=optimiser, loss='categorical_crossentropy', metrics=['accuracy'])

# Callbacks
checkpoint_cb = tf.keras.callbacks.ModelCheckpoint("plantnet_model.h5", save_best_only=True)
lr_schedule = tf.keras.callbacks.ReduceLROnPlateau(monitor='val_loss', factor=0.1, patience=3, verbose=1)
early_stopping = tf.keras.callbacks.EarlyStopping(monitor='val_loss', patience=5, restore_best_weights=True)


# Train the model
history = model.fit(train_dataset, epochs=10, validation_data=val_dataset, callbacks = [checkpoint_cb, lr_schedule, early_stopping])

test_loss, test_accuracy = model.evaluate(test_dataset)
print(f"Test Accuracy: {test_accuracy * 100:.2f}%")
