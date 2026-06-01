import java.sql.*;
import java.util.Scanner;

class MovieNotFound extends Exception{
    MovieNotFound(String msg){
        super(msg);
    }
}

class Movie{
    private int movieNo;
    private String title;
    private String genre;
    private int releasedDate;

    Movie(int movieNo, String title, String genre, int releasedDate){
        this.movieNo = movieNo;
        this.title = title;
        this.genre = genre;
        this.releasedDate = releasedDate;
    }
    public int getMovieNo(){return movieNo;}
    public String getTitle(){return title;}
    public String getGenre(){return genre;}
    public int getReleasedDate(){return releasedDate;}
}

class Review{
    private int reviewId;
    private double rating;
    private String experience;
    private int movieNo;

    Review(int reviewId, double rating, String experience, int movieNo){
        this.reviewId = reviewId;
        this.rating = rating;
        this.experience = experience;
        this.movieNo = movieNo;
    }
    public int getReviewId(){return reviewId;}
    public double getRating(){return rating;}
    public String getExperience(){return experience;}
    public int getMovieNo(){return movieNo;}
}

class MovieManagementSystem {
    Connection connection;

    MovieManagementSystem(String url, String username, String password) throws SQLException {
        this.connection = DriverManager.getConnection(url, username, password);
    }

    void addMovie(Movie m) throws SQLException {
        String sql = "insert into movie values(?,?,?,?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, m.getMovieNo());
        statement.setString(2, m.getTitle());
        statement.setString(3, m.getGenre());
        statement.setInt(4, m.getReleasedDate());
        statement.executeUpdate();
    }

    void addReview(Review r) throws SQLException{
        String sql = "insert into review values(?,?,?,?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1,r.getReviewId());
        statement.setDouble(2,r.getRating());
        statement.setString(3,r.getExperience());
        statement.setInt(4,r.getMovieNo());
        statement.executeUpdate();
    }

    void displayReviews(int movieNo) throws SQLException{
        String sql = "select * from review where m_no = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1,movieNo);
        ResultSet rs = statement.executeQuery();
        while (rs.next()){
            System.out.println(rs.getInt("review_id"));
            System.out.println(rs.getDouble("rating"));
            System.out.println(rs.getString("experience"));
        }
    }

    double getAverageRating(int movieNo) throws SQLException{
        String sql = "select avg(rating) from review where m_no = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1,movieNo);
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
            return rs.getDouble(1);
        }
        return 0.0;
        }

        void searchMovie(String title) throws SQLException, MovieNotFound{
            String sql = "select * from movie where title = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,title);
            ResultSet rs = statement.executeQuery();
            boolean found = false;
            while (rs.next()){
                found = true;
                System.out.println("Movie No:" + rs.getInt("movie_no"));
                System.out.println("Title:" + rs.getString("title"));
                System.out.println("Genre:" + rs.getString("genre"));
                System.out.println("Released Date:" + rs.getInt("released_date"));
            }
            if (!found){
                throw new MovieNotFound("Movie not found: " + title);
            }
        }

        void removeMovie(String title) throws SQLException, MovieNotFound{
            String sql = "delete from movie where title = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,title);
            int rows = statement.executeUpdate();
            if (rows == 0){
                throw new MovieNotFound("Movie not found: " + title);
            }
        }

        void updateMovie(int movieNo, String title, String genre, int releasedDate) throws SQLException, MovieNotFound{
            String sql = "update movie set title = ?, genre = ?, released_date = ? where movie_no = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,title);
            statement.setString(2,genre);
            statement.setInt(3,releasedDate);
            statement.setInt(4,movieNo);
            int rows = statement.executeUpdate();
            if (rows == 0){
                throw new MovieNotFound("Can't find the movie to update: " + movieNo + title + genre + releasedDate);
            }
        }
}

public class Main {
    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String url = "jdbc:mysql://localhost:3306/movie_review_system";
        String username = "root";
        String password = "0406";
        MovieManagementSystem system = new MovieManagementSystem(url, username, password);
        int choice;
        do{
            System.out.println("1. Add Movie");
            System.out.println("2. Add Review");
            System.out.println("3. Display Reviews");
            System.out.println("4. Search Movie");
            System.out.println("5. Delete the Movie");
            System.out.println("6. Get Average Rating");
            System.out.println("7. Update the Movie");
            System.out.println("8. Exit");
            choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice){
                case 1:
                    System.out.println("Enter the movie no:");
                    int movieNo = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Enter the movie name");
                    String name = scanner.nextLine();
                    if (name.isEmpty()){
                        System.out.println("Title cannot be empty");
                        break;
                    }
                    System.out.println("Enter the Genre");
                    String gerne = scanner.nextLine();
                    System.out.println("Enter the release date");
                    int date = scanner.nextInt();
                    scanner.nextLine();
                    if (date < 1888 || date > 2026){
                        System.out.println("Invalid date");
                        break;
                    }
                    Movie movie = new Movie(movieNo, name, gerne, date);
                    try{
                        system.addMovie(movie);
                        System.out.println("Movie added successfully");
                    }
                    catch (SQLException e){
                        System.out.println(e.getMessage());
                    }
                    break;

                case 2:
                    System.out.println("Enter the review id:");
                    int reviewId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Enter your Rating");
                    double rating = scanner.nextDouble();
                    scanner.nextLine();
                    if (rating < 1 || rating > 10){
                        System.out.println("Rating must be between 1 and 10!");
                        break;
                    }
                    System.out.println("Enter your Experience:");
                    String experience = scanner.nextLine();
                    System.out.println("Enter the Movie No:");
                    int mNo = scanner.nextInt();
                    scanner.nextLine();
                    Review review = new Review(reviewId, rating, experience, mNo);
                    try{
                        system.addReview(review);
                        System.out.println("Review added successfully");
                    }
                    catch (SQLException e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case 3:
                    System.out.println("Enter the Movie number:");
                    int movieNum = scanner.nextInt();
                    try {
                        system.displayReviews(movieNum);
                        System.out.println("Displaying the movies");
                    }
                    catch (SQLException e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case 4:
                    System.out.println("Enter the title:");
                    String title = scanner.nextLine();
                    try{
                        system.searchMovie(title);
                        System.out.println("Movie Founded successfully!");
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    } catch (MovieNotFound e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 5:
                    System.out.println("Enter the title:");
                    String movieName = scanner.nextLine();
                    try{
                        system.removeMovie(movieName);
                        System.out.println("Movie removed successfully");
                    }
                    catch (SQLException e){
                        System.out.println(e.getMessage());
                    }
                    catch (MovieNotFound e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case 6:
                    System.out.println("Enter the MovieNo:");
                    int movieNumber = scanner.nextInt();
                    try {
                       double avg =  system.getAverageRating(movieNumber);
                        System.out.println("Here is the average rating: " +avg);
                    }
                    catch (SQLException e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case 7:
                    System.out.println("Enter the title:");
                    String moviename = scanner.nextLine();
                    System.out.println("Enter the genre:");
                    String genre = scanner.nextLine();
                    System.out.println("Enter the released date:");
                    int rDate = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Enter the movie number:");
                    int movieNumb = scanner.nextInt();
                    scanner.nextLine();
                    try{
                        system.updateMovie(movieNumb, moviename, genre, rDate);
                        System.out.println("Movie updated Successfully");
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                    catch (MovieNotFound e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case 8:
                    System.out.println("See you!!!");
            }
        }while(choice != 8);
        system.connection.close();
        scanner.close();
    }
}
