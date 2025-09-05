import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;
import java.text.SimpleDateFormat;

/**
* Main Chatbot Application with GUI
* Professional AI Chatbot with NLP and Machine Learning capabilities
  */
  public class ChatbotApplication extends JFrame {
  private JTextArea chatArea;
  private JTextField inputField;
  private JButton sendButton;
  private ChatbotEngine chatbotEngine;
  private SimpleDateFormat timeFormat;

  public ChatbotApplication() {
  timeFormat = new SimpleDateFormat("HH:mm:ss");
  chatbotEngine = new ChatbotEngine();
  initializeGUI();
  loadTrainingData();
  }

  private void initializeGUI() {
  setTitle("Professional AI Chatbot");
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  setSize(800, 600);
  setLocationRelativeTo(null);

       // Create components
       chatArea = new JTextArea();
       chatArea.setEditable(false);
       chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
       chatArea.setBackground(new Color(248, 248, 248));
       chatArea.setMargin(new Insets(10, 10, 10, 10));

       JScrollPane scrollPane = new JScrollPane(chatArea);
       scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

       // Input panel
       JPanel inputPanel = new JPanel(new BorderLayout());
       inputField = new JTextField();
       inputField.setFont(new Font("Arial", Font.PLAIN, 14));
       sendButton = new JButton("Send");
       sendButton.setPreferredSize(new Dimension(80, 30));

       inputPanel.add(inputField, BorderLayout.CENTER);
       inputPanel.add(sendButton, BorderLayout.EAST);
       inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

       // Layout
       setLayout(new BorderLayout());
       add(scrollPane, BorderLayout.CENTER);
       add(inputPanel, BorderLayout.SOUTH);

       // Event listeners
       sendButton.addActionListener(new SendMessageListener());
       inputField.addActionListener(new SendMessageListener());

       // Welcome message
       appendMessage("Bot", "Hello! I'm your AI assistant. How can I help you today?");
  }

  private void loadTrainingData() {
  // Load FAQ and training data
  chatbotEngine.loadFAQData();
  appendMessage("System", "Chatbot initialized and training data loaded.");
  }

  private void appendMessage(String sender, String message) {
  String timestamp = timeFormat.format(new Date());
  chatArea.append(String.format("[%s] %s: %s\n", timestamp, sender, message));
  chatArea.setCaretPosition(chatArea.getDocument().getLength());
  }

  private class SendMessageListener implements ActionListener {
  @Override
  public void actionPerformed(ActionEvent e) {
  String userInput = inputField.getText().trim();
  if (!userInput.isEmpty()) {
  appendMessage("You", userInput);
  inputField.setText("");

               // Process message in background thread
               SwingUtilities.invokeLater(() -> {
                   String response = chatbotEngine.processMessage(userInput);
                   appendMessage("Bot", response);
               });
           }
       }
  }

  public static void main(String[] args) {
  SwingUtilities.invokeLater(() -> {
  try {
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
  } catch (Exception e) {
  e.printStackTrace();
  }
  new ChatbotApplication().setVisible(true);
  });
  }
  }

/**
* Core Chatbot Engine with NLP and ML capabilities
  */
  class ChatbotEngine {
  private Map<String, String> faqDatabase;
  private NLPProcessor nlpProcessor;
  private MLResponseGenerator mlGenerator;
  private ConversationContext context;

  public ChatbotEngine() {
  faqDatabase = new HashMap<>();
  nlpProcessor = new NLPProcessor();
  mlGenerator = new MLResponseGenerator();
  context = new ConversationContext();
  }

  public void loadFAQData() {
  // Load frequently asked questions and responses
  faqDatabase.put("hello", "Hello! How can I assist you today?");
  faqDatabase.put("hi", "Hi there! What can I do for you?");
  faqDatabase.put("help", "I'm here to help! You can ask me about various topics or just have a conversation.");
  faqDatabase.put("weather", "I don't have access to real-time weather data, but I'd recommend checking a weather app or website for current conditions.");
  faqDatabase.put("time", "The current time is: " + new Date().toString());
  faqDatabase.put("name", "I'm an AI chatbot created to assist you with various questions and tasks.");
  faqDatabase.put("how are you", "I'm functioning well, thank you for asking! How are you doing?");
  faqDatabase.put("goodbye", "Goodbye! It was nice chatting with you. Have a great day!");
  faqDatabase.put("bye", "See you later! Feel free to come back anytime if you need help.");
  faqDatabase.put("thank you", "You're welcome! I'm glad I could help.");
  faqDatabase.put("thanks", "No problem! Is there anything else I can assist you with?");

       // Technical FAQ
       faqDatabase.put("java", "Java is a popular programming language known for its portability and object-oriented features. What would you like to know about Java?");
       faqDatabase.put("programming", "Programming is the process of creating instructions for computers. I can help with various programming concepts and languages.");
       faqDatabase.put("ai", "Artificial Intelligence is the simulation of human intelligence in machines. I'm an example of a simple AI chatbot!");
       
       // Load additional training patterns
       mlGenerator.trainModel();
  }

  public String processMessage(String input) {
  // Update conversation context
  context.addUserMessage(input);

       // Process input through NLP
       ProcessedInput processedInput = nlpProcessor.process(input);
       
       // Try to find direct FAQ match first
       String faqResponse = findFAQMatch(processedInput);
       if (faqResponse != null) {
           context.addBotResponse(faqResponse);
           return faqResponse;
       }
       
       // Use ML-based response generation
       String mlResponse = mlGenerator.generateResponse(processedInput, context);
       context.addBotResponse(mlResponse);
       
       return mlResponse;
  }

  private String findFAQMatch(ProcessedInput input) {
  String normalizedInput = input.getNormalizedText();

       // Direct match
       if (faqDatabase.containsKey(normalizedInput)) {
           return faqDatabase.get(normalizedInput);
       }
       
       // Partial match with keywords
       for (String keyword : input.getKeywords()) {
           if (faqDatabase.containsKey(keyword)) {
               return faqDatabase.get(keyword);
           }
       }
       
       // Fuzzy matching for similar phrases
       for (Map.Entry<String, String> entry : faqDatabase.entrySet()) {
           if (calculateSimilarity(normalizedInput, entry.getKey()) > 0.7) {
               return entry.getValue();
           }
       }
       
       return null;
  }

  private double calculateSimilarity(String s1, String s2) {
  // Simple similarity calculation (Jaccard similarity)
  Set<String> words1 = new HashSet<>(Arrays.asList(s1.split(" ")));
  Set<String> words2 = new HashSet<>(Arrays.asList(s2.split(" ")));

       Set<String> intersection = new HashSet<>(words1);
       intersection.retainAll(words2);
       
       Set<String> union = new HashSet<>(words1);
       union.addAll(words2);
       
       return union.isEmpty() ? 0 : (double) intersection.size() / union.size();
  }
  }

/**
* Natural Language Processing component
  */
  class NLPProcessor {
  private Set<String> stopWords;

  public NLPProcessor() {
  initializeStopWords();
  }

  private void initializeStopWords() {
  stopWords = new HashSet<>(Arrays.asList(
  "the", "a", "an", "and", "or", "but", "in", "on", "at", "to", "for", "of", "with", "by",
  "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "do", "does", "did",
  "will", "would", "could", "should", "may", "might", "must", "shall", "can"
  ));
  }

  public ProcessedInput process(String input) {
  String normalizedText = input.toLowerCase().trim();

       // Remove punctuation and split into words
       String[] words = normalizedText.replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+");
       
       // Extract keywords (non-stop words)
       List<String> keywords = new ArrayList<>();
       for (String word : words) {
           if (!stopWords.contains(word) && word.length() > 2) {
               keywords.add(word);
           }
       }
       
       // Detect intent
       String intent = detectIntent(normalizedText, keywords);
       
       // Extract entities (simple implementation)
       List<String> entities = extractEntities(words);
       
       return new ProcessedInput(normalizedText, keywords, intent, entities);
  }

  private String detectIntent(String text, List<String> keywords) {
  // Simple rule-based intent detection
  if (text.contains("?")) {
  return "QUESTION";
  }
  if (containsAny(text, Arrays.asList("hello", "hi", "hey"))) {
  return "GREETING";
  }
  if (containsAny(text, Arrays.asList("bye", "goodbye", "see you"))) {
  return "FAREWELL";
  }
  if (containsAny(text, Arrays.asList("thank", "thanks"))) {
  return "GRATITUDE";
  }
  if (containsAny(text, Arrays.asList("help", "assist", "support"))) {
  return "REQUEST_HELP";
  }
  return "GENERAL";
  }

  private boolean containsAny(String text, List<String> phrases) {
  return phrases.stream().anyMatch(text::contains);
  }

  private List<String> extractEntities(String[] words) {
  List<String> entities = new ArrayList<>();
  // Simple entity extraction - look for capitalized words
  for (String word : words) {
  if (Character.isUpperCase(word.charAt(0)) && word.length() > 1) {
  entities.add(word);
  }
  }
  return entities;
  }
  }

/**
* Machine Learning Response Generator
  */
  class MLResponseGenerator {
  private Map<String, List<String>> intentResponses;
  private Random random;

  public MLResponseGenerator() {
  intentResponses = new HashMap<>();
  random = new Random();
  }

  public void trainModel() {
  // Training data for different intents
  intentResponses.put("GREETING", Arrays.asList(
  "Hello! How can I help you today?",
  "Hi there! What would you like to know?",
  "Hey! Great to see you. What's on your mind?"
  ));

       intentResponses.put("FAREWELL", Arrays.asList(
           "Goodbye! Have a wonderful day!",
           "See you later! Take care!",
           "Bye! Feel free to come back anytime!"
       ));
       
       intentResponses.put("QUESTION", Arrays.asList(
           "That's an interesting question. Let me think about that...",
           "I'd be happy to help you with that. Based on what I know...",
           "Great question! Here's what I can tell you..."
       ));
       
       intentResponses.put("GRATITUDE", Arrays.asList(
           "You're very welcome! Glad I could help!",
           "No problem at all! Is there anything else you'd like to know?",
           "Happy to help! Don't hesitate to ask if you have more questions."
       ));
       
       intentResponses.put("REQUEST_HELP", Arrays.asList(
           "I'm here to help! What specific assistance do you need?",
           "Of course! I'd be happy to assist you. What can I help with?",
           "Absolutely! Tell me more about what you need help with."
       ));
       
       intentResponses.put("GENERAL", Arrays.asList(
           "That's interesting! Tell me more about that.",
           "I see. Can you elaborate on that?",
           "Thanks for sharing! What else would you like to discuss?",
           "I understand. How can I assist you further?",
           "That's a good point. What are your thoughts on it?"
       ));
  }

  public String generateResponse(ProcessedInput input, ConversationContext context) {
  String intent = input.getIntent();

       // Get potential responses for the intent
       List<String> responses = intentResponses.get(intent);
       if (responses == null || responses.isEmpty()) {
           responses = intentResponses.get("GENERAL");
       }
       
       // Select response based on context and randomization
       String baseResponse = responses.get(random.nextInt(responses.size()));
       
       // Enhance response based on keywords
       String enhancedResponse = enhanceResponseWithKeywords(baseResponse, input.getKeywords());
       
       return enhancedResponse;
  }

  private String enhanceResponseWithKeywords(String baseResponse, List<String> keywords) {
  // Simple keyword-based enhancement
  if (keywords.contains("java")) {
  return baseResponse + " Java is a versatile programming language with many applications!";
  }
  if (keywords.contains("programming")) {
  return baseResponse + " Programming can be very rewarding once you get the hang of it!";
  }
  if (keywords.contains("help")) {
  return baseResponse + " I'm designed to be as helpful as possible!";
  }

       return baseResponse;
  }
  }

/**
* Processed Input data structure
  */
  class ProcessedInput {
  private String normalizedText;
  private List<String> keywords;
  private String intent;
  private List<String> entities;

  public ProcessedInput(String normalizedText, List<String> keywords, String intent, List<String> entities) {
  this.normalizedText = normalizedText;
  this.keywords = keywords;
  this.intent = intent;
  this.entities = entities;
  }

  // Getters
  public String getNormalizedText() { return normalizedText; }
  public List<String> getKeywords() { return keywords; }
  public String getIntent() { return intent; }
  public List<String> getEntities() { return entities; }
  }

/**
* Conversation Context for maintaining chat history
  */
  class ConversationContext {
  private List<String> userMessages;
  private List<String> botResponses;
  private int maxHistorySize = 10;

  public ConversationContext() {
  userMessages = new ArrayList<>();
  botResponses = new ArrayList<>();
  }

  public void addUserMessage(String message) {
  userMessages.add(message);
  if (userMessages.size() > maxHistorySize) {
  userMessages.remove(0);
  }
  }

  public void addBotResponse(String response) {
  botResponses.add(response);
  if (botResponses.size() > maxHistorySize) {
  botResponses.remove(0);
  }
  }

  public List<String> getUserMessages() { return new ArrayList<>(userMessages); }
  public List<String> getBotResponses() { return new ArrayList<>(botResponses); }
  public String getLastUserMessage() {
  return userMessages.isEmpty() ? "" : userMessages.get(userMessages.size() - 1);
  }
  }