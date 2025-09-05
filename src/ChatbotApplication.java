import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Professional AI Chatbot with Internet Connectivity
 * Enhanced with intelligent response generation and web search capabilities
 */
public class ChatbotApplication extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JCheckBox internetCheckBox;
    private ProfessionalChatbotEngine chatbotEngine;
    private SimpleDateFormat timeFormat;

    public ChatbotApplication() {
        timeFormat = new SimpleDateFormat("HH:mm:ss");
        chatbotEngine = new ProfessionalChatbotEngine();
        initializeGUI();
        loadAdvancedTrainingData();
    }

    private void initializeGUI() {
        setTitle("Professional AI Assistant");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        // Create components
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 13));
        chatArea.setBackground(new Color(250, 250, 252));
        chatArea.setMargin(new Insets(15, 15, 15, 15));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Conversation"));

        // Input panel with internet option
        JPanel inputPanel = new JPanel(new BorderLayout());
        JPanel topInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        internetCheckBox = new JCheckBox("Enable Internet Search", true);
        internetCheckBox.setToolTipText("Allow the bot to search online for current information");
        topInputPanel.add(internetCheckBox);

        JPanel bottomInputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        sendButton = new JButton("Send");
        sendButton.setPreferredSize(new Dimension(80, 35));
        sendButton.setBackground(new Color(0, 123, 255));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);

        bottomInputPanel.add(inputField, BorderLayout.CENTER);
        bottomInputPanel.add(sendButton, BorderLayout.EAST);

        inputPanel.add(topInputPanel, BorderLayout.NORTH);
        inputPanel.add(bottomInputPanel, BorderLayout.CENTER);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Layout
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        // Event listeners
        sendButton.addActionListener(new SendMessageListener());
        inputField.addActionListener(new SendMessageListener());

        // Professional welcome message
        appendMessage("Assistant", "Hello! I'm your professional AI assistant. I can help you with:\n" +
                "• General questions and information\n" +
                "• Technical topics and programming\n" +
                "• Current events (with internet search)\n" +
                "• Business and professional advice\n" +
                "• Educational content\n\n" +
                "How may I assist you today?");
    }

    private void loadAdvancedTrainingData() {
        chatbotEngine.loadProfessionalKnowledgeBase();
        appendMessage("System", "Professional knowledge base loaded. Ready to assist!");
    }

    private void appendMessage(String sender, String message) {
        String timestamp = timeFormat.format(new Date());
        String prefix = sender.equals("You") ? "👤 " : (sender.equals("Assistant") ? "🤖 " : "⚙️ ");
        chatArea.append(String.format("[%s] %s%s: %s\n\n", timestamp, prefix, sender, message));
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    private class SendMessageListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String userInput = inputField.getText().trim();
            if (!userInput.isEmpty()) {
                appendMessage("You", userInput);
                inputField.setText("");
                sendButton.setEnabled(false);
                sendButton.setText("...");

                // Process in background thread
                SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                    protected String doInBackground() throws Exception {
                        return chatbotEngine.processMessage(userInput, internetCheckBox.isSelected());
                    }

                    protected void done() {
                        try {
                            String response = get();
                            appendMessage("Assistant", response);
                        } catch (Exception ex) {
                            appendMessage("Assistant", "I apologize, but I encountered an error processing your request. Please try again.");
                        }
                        sendButton.setEnabled(true);
                        sendButton.setText("Send");
                        inputField.requestFocus();
                    }
                };
                worker.execute();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ChatbotApplication().setVisible(true);
            }
        });
    }
}

/**
 * Professional Chatbot Engine with Internet Search and Advanced AI
 */
class ProfessionalChatbotEngine {
    private Map<String, String> knowledgeBase;
    private AdvancedNLPProcessor nlpProcessor;
    private IntelligentResponseGenerator responseGenerator;
    private WebSearchEngine webSearch;
    private ConversationMemory memory;

    public ProfessionalChatbotEngine() {
        knowledgeBase = new HashMap<String, String>();
        nlpProcessor = new AdvancedNLPProcessor();
        responseGenerator = new IntelligentResponseGenerator();
        webSearch = new WebSearchEngine();
        memory = new ConversationMemory();
    }

    public void loadProfessionalKnowledgeBase() {
        // Technical Knowledge
        knowledgeBase.put("java programming", "Java is a robust, object-oriented programming language. Key features include platform independence, strong memory management, and extensive libraries. Popular frameworks include Spring, Hibernate, and Maven for enterprise development.");
        knowledgeBase.put("python programming", "Python is a versatile, high-level programming language known for its readability and extensive libraries. It's widely used in data science, web development, automation, and AI/ML applications.");
        knowledgeBase.put("artificial intelligence", "AI involves creating systems that can perform tasks typically requiring human intelligence. This includes machine learning, natural language processing, computer vision, and robotics.");
        knowledgeBase.put("machine learning", "Machine learning is a subset of AI that enables systems to learn and improve from data without explicit programming. Common algorithms include neural networks, decision trees, and clustering.");

        // Business Knowledge
        knowledgeBase.put("project management", "Effective project management involves planning, executing, and monitoring projects to achieve specific goals within constraints. Popular methodologies include Agile, Scrum, and Waterfall.");
        knowledgeBase.put("data analysis", "Data analysis involves examining, cleaning, and modeling data to discover insights and support decision-making. Tools include Excel, SQL, Python, R, and visualization platforms like Tableau.");

        // General Knowledge
        knowledgeBase.put("climate change", "Climate change refers to long-term shifts in global temperatures and weather patterns. It's primarily driven by human activities and greenhouse gas emissions, requiring urgent global action.");
        knowledgeBase.put("renewable energy", "Renewable energy sources include solar, wind, hydroelectric, and geothermal power. These sustainable alternatives are crucial for reducing carbon emissions and combating climate change.");

        responseGenerator.loadAdvancedPatterns();
    }

    public String processMessage(String input, boolean useInternet) {
        memory.addUserMessage(input);

        // Advanced NLP processing
        ProcessedQuery query = nlpProcessor.analyzeQuery(input);

        // Check for direct knowledge base match
        String knowledgeResponse = findKnowledgeMatch(query);
        if (knowledgeResponse != null) {
            String response = responseGenerator.enhanceResponse(knowledgeResponse, query);
            memory.addAssistantResponse(response);
            return response;
        }

        // Use internet search if enabled and query seems to need current info
        if (useInternet && query.needsCurrentInfo()) {
            try {
                String searchResult = webSearch.searchAndSummarize(query.getSearchTerms());
                if (searchResult != null && !searchResult.trim().isEmpty()) {
                    String response = responseGenerator.formatSearchResponse(searchResult, query);
                    memory.addAssistantResponse(response);
                    return response;
                }
            } catch (Exception e) {
                // Fall back to knowledge-based response if internet search fails
            }
        }

        // Generate intelligent response based on context
        String response = responseGenerator.generateContextualResponse(query, memory);
        memory.addAssistantResponse(response);
        return response;
    }

    private String findKnowledgeMatch(ProcessedQuery query) {
        String normalizedQuery = query.getNormalizedText();

        // Direct match
        for (Map.Entry<String, String> entry : knowledgeBase.entrySet()) {
            if (normalizedQuery.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        // Keyword-based matching
        for (String keyword : query.getKeywords()) {
            for (Map.Entry<String, String> entry : knowledgeBase.entrySet()) {
                if (entry.getKey().contains(keyword)) {
                    return entry.getValue();
                }
            }
        }

        return null;
    }
}

/**
 * Advanced NLP Processor for better understanding
 */
class AdvancedNLPProcessor {
    private Set<String> stopWords;
    private Map<String, String> synonyms;

    public AdvancedNLPProcessor() {
        initializeStopWords();
        initializeSynonyms();
    }

    private void initializeStopWords() {
        stopWords = new HashSet<String>(Arrays.asList(
                "the", "a", "an", "and", "or", "but", "in", "on", "at", "to", "for", "of", "with", "by",
                "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "do", "does", "did",
                "will", "would", "could", "should", "may", "might", "must", "shall", "can", "i", "you", "he", "she", "it", "we", "they"
        ));
    }

    private void initializeSynonyms() {
        synonyms = new HashMap<String, String>();
        synonyms.put("how", "what");
        synonyms.put("explain", "describe");
        synonyms.put("tell", "explain");
        synonyms.put("show", "explain");
        synonyms.put("help", "assist");
        synonyms.put("ai", "artificial intelligence");
        synonyms.put("ml", "machine learning");
        synonyms.put("programming", "coding");
    }

    public ProcessedQuery analyzeQuery(String input) {
        String normalizedText = input.toLowerCase().trim();

        // Apply synonym replacement
        for (Map.Entry<String, String> synonym : synonyms.entrySet()) {
            normalizedText = normalizedText.replaceAll("\\b" + synonym.getKey() + "\\b", synonym.getValue());
        }

        // Extract keywords
        String[] words = normalizedText.replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+");
        List<String> keywords = new ArrayList<String>();

        for (String word : words) {
            if (!stopWords.contains(word) && word.length() > 2) {
                keywords.add(word);
            }
        }

        // Detect query type and intent
        QueryType queryType = detectQueryType(input, normalizedText);
        String intent = detectIntent(normalizedText, keywords);
        boolean needsCurrent = needsCurrentInformation(normalizedText);

        return new ProcessedQuery(normalizedText, keywords, queryType, intent, needsCurrent);
    }

    private QueryType detectQueryType(String original, String normalized) {
        if (original.contains("?")) return QueryType.QUESTION;
        if (containsAny(normalized, Arrays.asList("explain", "describe", "what is", "how does", "tell me about"))) return QueryType.EXPLANATION;
        if (containsAny(normalized, Arrays.asList("help", "assist", "support", "guide"))) return QueryType.HELP_REQUEST;
        if (containsAny(normalized, Arrays.asList("hello", "hi", "hey", "good morning", "good afternoon"))) return QueryType.GREETING;
        if (containsAny(normalized, Arrays.asList("bye", "goodbye", "see you", "farewell"))) return QueryType.FAREWELL;
        if (containsAny(normalized, Arrays.asList("thank", "thanks", "appreciate"))) return QueryType.GRATITUDE;
        return QueryType.STATEMENT;
    }

    private String detectIntent(String text, List<String> keywords) {
        if (containsAny(text, Arrays.asList("programming", "coding", "java", "python", "software"))) return "TECHNICAL";
        if (containsAny(text, Arrays.asList("business", "management", "strategy", "marketing"))) return "BUSINESS";
        if (containsAny(text, Arrays.asList("science", "research", "study", "analysis"))) return "ACADEMIC";
        if (containsAny(text, Arrays.asList("news", "current", "latest", "recent", "today"))) return "CURRENT_EVENTS";
        return "GENERAL";
    }

    private boolean needsCurrentInformation(String text) {
        return containsAny(text, Arrays.asList("current", "latest", "recent", "news", "today", "now", "2024", "2025"));
    }

    private boolean containsAny(String text, List<String> phrases) {
        for (String phrase : phrases) {
            if (text.contains(phrase)) return true;
        }
        return false;
    }
}

/**
 * Web Search Engine for current information
 */
class WebSearchEngine {

    public String searchAndSummarize(String query) {
        try {
            // Simple web search simulation (replace with actual API in production)
            String searchQuery = query.replace(" ", "+");
            URL url = new URL("https://api.duckduckgo.com/?q=" + searchQuery + "&format=json&no_html=1");

            // In a real implementation, you would:
            // 1. Use a proper search API (Google Custom Search, Bing API, etc.)
            // 2. Parse JSON responses
            // 3. Extract relevant snippets
            // 4. Summarize the content

            // For demo purposes, return a professional response
            return generateSearchResponse(query);

        } catch (Exception e) {
            return null;
        }
    }

    private String generateSearchResponse(String query) {
        // Professional responses for common search types
        if (query.contains("weather")) {
            return "I don't have access to real-time weather data. I recommend checking a reliable weather service like Weather.com or your local meteorological service for current conditions and forecasts.";
        }
        if (query.contains("news") || query.contains("current events")) {
            return "For the latest news and current events, I recommend checking reputable news sources such as BBC, Reuters, AP News, or your preferred local news outlets for the most up-to-date information.";
        }
        if (query.contains("stock") || query.contains("market")) {
            return "For current stock prices and market information, please consult financial platforms like Yahoo Finance, Bloomberg, or MarketWatch for real-time data and analysis.";
        }
        return "I found relevant information online, but recommend verifying current details from authoritative sources for the most accurate and up-to-date information.";
    }
}

/**
 * Intelligent Response Generator
 */
class IntelligentResponseGenerator {
    private Map<String, List<String>> professionalResponses;
    private Random random;

    public IntelligentResponseGenerator() {
        professionalResponses = new HashMap<String, List<String>>();
        random = new Random();
    }

    public void loadAdvancedPatterns() {
        professionalResponses.put("GREETING", Arrays.asList(
                "Hello! I'm here to provide professional assistance. How may I help you today?",
                "Good day! I'm ready to assist you with any questions or tasks you have.",
                "Welcome! I'm your AI assistant, equipped to help with various professional and technical topics."
        ));

        professionalResponses.put("TECHNICAL", Arrays.asList(
                "That's an excellent technical question. Based on current best practices and industry standards,",
                "From a technical perspective, let me provide you with a comprehensive explanation:",
                "This is a relevant technical topic. Here's what you should know:"
        ));

        professionalResponses.put("BUSINESS", Arrays.asList(
                "From a business strategy standpoint,",
                "In professional business contexts,",
                "This is an important business consideration:"
        ));

        professionalResponses.put("EXPLANATION", Arrays.asList(
                "I'd be happy to explain this concept in detail:",
                "Let me break this down for you systematically:",
                "Here's a comprehensive explanation of this topic:"
        ));
    }

    public String enhanceResponse(String baseResponse, ProcessedQuery query) {
        String intent = query.getIntent();
        List<String> intros = professionalResponses.get(intent);

        if (intros != null && !intros.isEmpty()) {
            String intro = intros.get(random.nextInt(intros.size()));
            return intro + " " + baseResponse;
        }

        return baseResponse;
    }

    public String formatSearchResponse(String searchResult, ProcessedQuery query) {
        return "Based on current information: " + searchResult + "\n\nPlease note that online information should be verified from official sources for critical decisions.";
    }

    public String generateContextualResponse(ProcessedQuery query, ConversationMemory memory) {
        String intent = query.getIntent();
        QueryType type = query.getQueryType();

        switch (type) {
            case GREETING:
                return getRandomResponse("GREETING");
            case QUESTION:
            case EXPLANATION:
                return generateExplanationResponse(query);
            case HELP_REQUEST:
                return "I'm here to help! Could you please provide more specific details about what you need assistance with? I can help with technical topics, business questions, general information, and much more.";
            case FAREWELL:
                return "Thank you for using my services! I hope I was able to help. Feel free to return anytime you need assistance. Have a great day!";
            case GRATITUDE:
                return "You're very welcome! I'm glad I could assist you. If you have any other questions or need further help, please don't hesitate to ask.";
            default:
                return generateIntelligentResponse(query, intent);
        }
    }

    private String generateExplanationResponse(ProcessedQuery query) {
        List<String> keywords = query.getKeywords();
        if (keywords.isEmpty()) {
            return "I'd be happy to explain that topic. Could you please provide more specific details about what you'd like to know?";
        }

        String topic = String.join(" ", keywords);
        return "I understand you're asking about " + topic + ". While I don't have specific information readily available on this exact topic, I recommend consulting authoritative sources or academic resources for detailed information. Is there a particular aspect you'd like me to help you explore further?";
    }

    private String generateIntelligentResponse(ProcessedQuery query, String intent) {
        switch (intent) {
            case "TECHNICAL":
                return "This appears to be a technical question. For accurate and up-to-date technical information, I recommend consulting official documentation, technical forums, or industry-standard resources. Could you provide more context so I can offer more targeted guidance?";
            case "BUSINESS":
                return "Business decisions often require current market data and specific context. While I can provide general guidance, I recommend consulting with professionals or current business resources for strategic decisions. What specific aspect can I help clarify?";
            case "CURRENT_EVENTS":
                return "For current events and latest news, I recommend checking reputable news sources for the most accurate and timely information. Is there a particular topic or area you're interested in learning about?";
            default:
                return "That's an interesting topic. While I may not have comprehensive information on this specific subject, I'm here to help in any way I can. Could you provide more details or ask a more specific question so I can better assist you?";
        }
    }

    private String getRandomResponse(String category) {
        List<String> responses = professionalResponses.get(category);
        if (responses != null && !responses.isEmpty()) {
            return responses.get(random.nextInt(responses.size()));
        }
        return "I'm here to help you with any questions or tasks you have.";
    }
}

/**
 * Enhanced data structures
 */
enum QueryType {
    QUESTION, EXPLANATION, HELP_REQUEST, GREETING, FAREWELL, GRATITUDE, STATEMENT
}

class ProcessedQuery {
    private String normalizedText;
    private List<String> keywords;
    private QueryType queryType;
    private String intent;
    private boolean needsCurrentInfo;

    public ProcessedQuery(String normalizedText, List<String> keywords, QueryType queryType, String intent, boolean needsCurrentInfo) {
        this.normalizedText = normalizedText;
        this.keywords = keywords;
        this.queryType = queryType;
        this.intent = intent;
        this.needsCurrentInfo = needsCurrentInfo;
    }

    public String getNormalizedText() { return normalizedText; }
    public List<String> getKeywords() { return keywords; }
    public QueryType getQueryType() { return queryType; }
    public String getIntent() { return intent; }
    public boolean needsCurrentInfo() { return needsCurrentInfo; }
    public String getSearchTerms() { return String.join(" ", keywords); }
}

class ConversationMemory {
    private List<String> userMessages;
    private List<String> assistantResponses;
    private int maxMemory = 5;

    public ConversationMemory() {
        userMessages = new ArrayList<String>();
        assistantResponses = new ArrayList<String>();
    }

    public void addUserMessage(String message) {
        userMessages.add(message);
        if (userMessages.size() > maxMemory) {
            userMessages.remove(0);
        }
    }

    public void addAssistantResponse(String response) {
        assistantResponses.add(response);
        if (assistantResponses.size() > maxMemory) {
            assistantResponses.remove(0);
        }
    }

    public List<String> getRecentUserMessages() { return new ArrayList<String>(userMessages); }
    public List<String> getRecentResponses() { return new ArrayList<String>(assistantResponses); }
}