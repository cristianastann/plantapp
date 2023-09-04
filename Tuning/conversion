import tensorflow as tf

# Load the model
model = tf.keras.models.load_model('plantnet_model.h5')

# Convert to TFLite
converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()

with open('plantnet_model.tflite', 'wb') as f:
  f.write(tflite_model)
