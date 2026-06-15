package com.feedbackhub.config;

import com.feedbackhub.entity.Score;
import com.feedbackhub.entity.User;
import com.feedbackhub.enums.FeedbackStatus;
import com.feedbackhub.enums.TrendDirection;
import com.feedbackhub.enums.UserRole;
import com.feedbackhub.repository.ScoreRepository;
import com.feedbackhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ScoreRepository scoreRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (userRepo.count() > 0) return;

        System.out.println("Seeding demo data...");

        // Trainers
        User trainer = new User();
        trainer.setFullName("Trainer Alex");
        trainer.setEmail("trainer@fh.com");
        trainer.setPassword(encoder.encode("password"));
        trainer.setRole(UserRole.TRAINER);
        trainer.setDepartment("Training");
        trainer.setActive(true);
        trainer = userRepo.save(trainer);

        // Trainees
        User t1 = new User();
        t1.setFullName("Ravi Kumar");
        t1.setEmail("ravi@fh.com");
        t1.setPassword(encoder.encode("password"));
        t1.setRole(UserRole.TRAINEE);
        t1.setPodName("Pod C");
        t1.setCohortName("Cohort 12");
        t1.setActive(true);
        t1 = userRepo.save(t1);

        User t2 = new User();
        t2.setFullName("Divya Sharma");
        t2.setEmail("divya@fh.com");
        t2.setPassword(encoder.encode("password"));
        t2.setRole(UserRole.TRAINEE);
        t2.setPodName("Pod C");
        t2.setCohortName("Cohort 12");
        t2.setActive(true);
        t2 = userRepo.save(t2);

        User t3 = new User();
        t3.setFullName("Kiran Menon");
        t3.setEmail("kiran@fh.com");
        t3.setPassword(encoder.encode("password"));
        t3.setRole(UserRole.TRAINEE);
        t3.setPodName("Pod D");
        t3.setCohortName("Cohort 12");
        t3.setActive(true);
        t3 = userRepo.save(t3);

        User t4 = new User();
        t4.setFullName("Suresh Babu");
        t4.setEmail("suresh@fh.com");
        t4.setPassword(encoder.encode("password"));
        t4.setRole(UserRole.TRAINEE);
        t4.setPodName("Pod D");
        t4.setCohortName("Cohort 12");
        t4.setActive(true);
        t4 = userRepo.save(t4);

        User t5 = new User();
        t5.setFullName("Meena Thomas");
        t5.setEmail("meena@fh.com");
        t5.setPassword(encoder.encode("password"));
        t5.setRole(UserRole.TRAINEE);
        t5.setPodName("Pod A");
        t5.setCohortName("Cohort 11");
        t5.setActive(true);
        t5 = userRepo.save(t5);

        // Scores for Ravi
        String[] assignments = {"Sprint Planning Review","API Design","Unit Test Coverage","Code Review","Standup Observation","Sprint Retrospective","Design Patterns Quiz","Peer Review Task"};
        double[] rScores     = {82, 78, 44, 75, 88, 70, 58, 72};
        String[] categories  = {"Project Mgmt","Technical","Testing","Technical","Communication","Process","Technical","Collaboration"};
        String[] grades      = {"B","B+","D","B","A","B-","C+","B"};
        String[] weeks       = {"W1","W2","W3","W4","W5","W6","W7","W8"};

        for (int i = 0; i < assignments.length; i++) {
            Score s = new Score();
            s.setTrainee(t1);
            s.setTrainer(trainer);
            s.setAssignmentName(assignments[i]);
            s.setCategory(categories[i]);
            s.setScore(rScores[i]);
            s.setGrade(grades[i]);
            s.setSubmittedDate(LocalDate.now().minusDays(30 - i * 3));
            s.setWeekLabel(weeks[i]);
            s.setFeedbackStatus(i % 3 == 0 ? FeedbackStatus.PENDING : FeedbackStatus.VIEWED);
            s.setTrend(rScores[i] >= 70 ? TrendDirection.UP : TrendDirection.DOWN);
            scoreRepo.save(s);
        }

        // Scores for Divya
        double[] dScores = {68, 72, 65, 70};
        for (int i = 0; i < dScores.length; i++) {
            Score s = new Score();
            s.setTrainee(t2);
            s.setTrainer(trainer);
            s.setAssignmentName("Assignment " + (i + 1));
            s.setCategory("Technical");
            s.setScore(dScores[i]);
            s.setGrade("B-");
            s.setSubmittedDate(LocalDate.now().minusDays(20 - i * 4));
            s.setWeekLabel("W" + (i + 1));
            s.setFeedbackStatus(FeedbackStatus.PENDING);
            s.setTrend(TrendDirection.STABLE);
            scoreRepo.save(s);
        }

        // Low score for Suresh (should trigger alert)
        Score low = new Score();
        low.setTrainee(t4);
        low.setTrainer(trainer);
        low.setAssignmentName("API Testing Module");
        low.setCategory("Testing");
        low.setScore(44.0);
        low.setGrade("D");
        low.setSubmittedDate(LocalDate.now().minusDays(5));
        low.setWeekLabel("W3");
        low.setFeedbackStatus(FeedbackStatus.PENDING);
        low.setTrend(TrendDirection.DOWN);
        scoreRepo.save(low);

        System.out.println("Demo data seeded. Login: trainer@fh.com / ravi@fh.com / divya@fh.com (password: password)");
    }
}
