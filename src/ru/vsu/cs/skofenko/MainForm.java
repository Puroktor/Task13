package ru.vsu.cs.skofenko;

import ru.vsu.cs.skofenko.logic.*;
import ru.vsu.cs.util.DrawUtils;
import ru.vsu.cs.util.JTableUtils;
import ru.vsu.cs.util.SwingUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;

public class MainForm extends JFrame {

    private JPanel mainPanel;
    private JTable gameField;
    private JButton newGameButton;
    private JLabel score;
    private JLabel bestScore;
    private JSlider sizeSlider;
    private JScrollPane jScrollPane;
    private final JLabel winOrLoseLabel = initLabel();

    private final int DEFAULT_CELL_SIZE = 50;
    private final int DEFAULT_ROW_COUNT = 4;
    private final int DEFAULT_COLUMN_COUNT = 4;


    private GameLogic logic;
    private Font font = null;

    private JLabel initLabel() {
        JLabel label = new JLabel();
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(getFont(28));
        return label;
    }

    private Font getFont(int size) {
        if (font == null || font.getSize() != size) {
            font = new Font("Arial", Font.BOLD, size);
        }
        return font;
    }

    public MainForm() {
        this.setTitle("2048");
        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.pack();

        mainPanel.setFocusable(true);
        newGameButton.setFocusable(false);

        bestScore.setText(String.valueOf(WriterReader.readBestScore()));

        gameField.setRowHeight(DEFAULT_CELL_SIZE);
        JTableUtils.initJTableForArray(gameField, DEFAULT_CELL_SIZE, false, false, false, false);
        gameField.setIntercellSpacing(new Dimension(0, 0));

        initSizeSlider();

        startNewGame();

        gameField.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            final class DrawComponent extends Component {
                private int row = 0, column = 0;

                @Override
                public void paint(Graphics gr) {
                    Graphics2D g2d = (Graphics2D) gr;
                    int width = getWidth() - 1;
                    int height = getHeight() - 1;
                    GameCell cell = logic.getCell(row, column);
                    paintCell(cell, g2d, width, height);
                }
            }

            DrawComponent comp = new DrawComponent();

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                comp.row = row;
                comp.column = column;
                return comp;
            }
        });
        updateWindowSize();

        newGameButton.addActionListener(e -> startNewGame());

        sizeSlider.addChangeListener(e -> {
            int value = ((JSlider) e.getSource()).getValue();
            JTableUtils.resizeJTable(gameField, gameField.getRowCount(), gameField.getColumnCount(), DEFAULT_CELL_SIZE * value,
                    DEFAULT_CELL_SIZE * value);
        });

        mainPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (logic.getGameState() == GameState.PLAYING) {
                    boolean needToUpdate = false;
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP -> needToUpdate = logic.shift(Direction.UP);
                        case KeyEvent.VK_DOWN -> needToUpdate = logic.shift(Direction.DOWN);
                        case KeyEvent.VK_LEFT -> needToUpdate = logic.shift(Direction.LEFT);
                        case KeyEvent.VK_RIGHT -> needToUpdate = logic.shift(Direction.RIGHT);
                    }
                    if (needToUpdate)
                        updateView();
                }
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                WriterReader.writeBestScore(bestScore.getText());
            }
        });

    }

    private void initSizeSlider() {
        sizeSlider.setMinimum(1);
        sizeSlider.setMaximum(5);
        sizeSlider.setValue(1);
        sizeSlider.setMinorTickSpacing(1);
        sizeSlider.setMajorTickSpacing(4);
        sizeSlider.setSnapToTicks(true);
        sizeSlider.setFocusable(false);
    }

    private void paintCell(GameCell cell, Graphics2D g2d, int width, int height) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        int size = Math.min(width, height);

        Color backColor = Colors.EMPTYCOLOR;
        String str = "";
        if (!cell.isEmpty()) {
            if (cell.getValue() == 2) {
                backColor = Colors.COLOR2;
            } else if (cell.getValue() == 4) {
                backColor = Colors.COLOR4;
            } else if (cell.getValue() == 8) {
                backColor = Colors.COLOR8;
            } else if (cell.getValue() == 16) {
                backColor = Colors.COLOR16;
            } else if (cell.getValue() == 32) {
                backColor = Colors.COLOR32;
            } else if (cell.getValue() == 64) {
                backColor = Colors.COLOR64;
            } else if (cell.getValue() == 128) {
                backColor = Colors.COLOR128;
            } else if (cell.getValue() == 256) {
                backColor = Colors.COLOR256;
            } else if (cell.getValue() == 512) {
                backColor = Colors.COLOR512;
            } else if (cell.getValue() == 1024) {
                backColor = Colors.COLOR1024;
            } else if (cell.getValue() == 2048) {
                backColor = Colors.COLOR2048;
            }
            str = String.valueOf(cell.getValue());
        }
        int bound = (int) Math.round(size * 0.1);
        g2d.setColor(backColor);
        g2d.fillRect(0, 0, width, height);

        Color color = DrawUtils.getContrastColor(backColor);
        g2d.setColor(color);
        DrawUtils.drawStringInCenter(g2d, getFont((int) ((size - 2 * bound) * 0.6)), str, 0, 0, width, height);
    }

    private void startNewGame() {
        jScrollPane.setViewport(new JViewport());
        jScrollPane.getViewport().add(gameField);
        logic = new GameLogic(DEFAULT_ROW_COUNT, DEFAULT_COLUMN_COUNT);
        JTableUtils.resizeJTable(gameField, logic.getRowCount(), logic.getColumnCount(), gameField.getRowHeight(),
                gameField.getRowHeight());
        updateView();
    }

    private void updateWindowSize() {
        SwingUtils.setFixedSize(this, gameField.getWidth() + 200, gameField.getHeight() + 300);
    }

    private void updateView() {
        gameField.repaint();
        score.setText(String.valueOf(logic.getScore()));
        if (Integer.parseInt(bestScore.getText()) < logic.getScore()) {
            bestScore.setText(String.valueOf(logic.getScore()));
        }
        if (logic.getGameState() != GameState.PLAYING) {
            if (logic.getGameState() == GameState.LOSE) {
                winOrLoseLabel.setText("Вы проиграли!");
            } else {
                winOrLoseLabel.setText("Вы выиграли!");
            }
            ActionListener taskPerformer = evt -> {
                jScrollPane.setViewport(new JViewport());
                jScrollPane.getViewport().add(winOrLoseLabel, null);
            };
            Timer tim = new Timer(1000, taskPerformer);
            tim.start();
            tim.setRepeats(false);
        }
    }
}
