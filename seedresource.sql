use admagent;
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS resource (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description LONGTEXT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

START TRANSACTION;

INSERT INTO resource (id, name, category, description)
VALUES

(1, 'B.Tech Computer Science and Engineering (CSE)', 'COURSE',
'MIT-ADT University, Pune. School of Computing. Programme: B.Tech Computer Science and Engineering Core.

FEES: Published Indian-resident academic fees for the 2026-27 intake: Year 1 INR 287000; Year 2 INR 315000; Year 3 INR 335000; Year 4 INR 335000; total INR 1272000. Published approved-EWS total INR 732500. EWS amounts require eligibility verification and approval. Hostel and mess charges are separate.

ELIGIBILITY: HSC/12th or equivalent with Physics and Mathematics plus Chemistry, Biotechnology, Biology or a Technical Vocational subject. Minimum 50% in the specified subject group; 45% for applicable Maharashtra backward-class and PwD candidates. IB applicants have separate conditions.

SELECTION: Maharashtra applicants through MHT-CET 2026; All India applicants through JEE Main 2026. Other state entrance examinations, Uni-GAUGE or PERA may be considered for vacancies after CET/JEE applications are exhausted. Published departmental application fee INR 1800. Admission involves application, documents, selection, original-document verification and required fee payment.

PLACEMENTS: School of Computing published combined CSE/IT statistics for 2025-26: 679 total students, 555 placed, placement rate 81.73%, internships 307. These are school-level figures, not CSE-Core-specific results. Verified current CSE-Core average package unavailable. Historical university brochure reports INR 7.5 LPA average, but exact cohort year is unverified; it is not a current CSE average.

SCHOLARSHIPS: University ordinance effective 2026-27 includes Merit Excellence, Merit Achiever, Merit Performer, Merit-Based Support, verified financial-need tuition support, Earn and Learn, eligible staff-family concessions and external scholarships. Verified competition/sports achievements may receive 10% of the relevant academic-year tuition fee, subject to caps and conditions. Exact merit thresholds and renewal rules require confirmation. Awards and EWS concessions are not automatic.

No admission, scholarship, placement or salary guarantee. Offline counselling booking is not admission confirmation. Information checked 2026-10-10.'),

(2, 'B.Tech CSE - Artificial Intelligence and Edge Computing', 'COURSE',
'MIT-ADT University, Pune. School of Computing. Officially listed CSE specialisation: Artificial Intelligence and Edge Computing.

FEES: Central 2026-27 fee table publishes a common CSE heading: Year 1 INR 287000; Year 2 INR 315000; Year 3 INR 335000; Year 4 INR 335000; total INR 1272000; approved-EWS total INR 732500. A separate specialisation fee row was not found. Confirm applicability before quoting these as exact specialisation fees. Hostel/mess separate.

ELIGIBILITY: HSC/12th or equivalent with Physics and Mathematics plus Chemistry, Biotechnology, Biology or Technical Vocational subject; minimum 50% in specified subjects, or 45% for applicable Maharashtra backward-class/PwD candidates. IB conditions differ.

SELECTION: Maharashtra merit through MHT-CET 2026; All India through JEE Main 2026. Other state tests, Uni-GAUGE or PERA may be considered for remaining vacancies after CET/JEE applications. Published application fee INR 1800; documents, selection, verification and fee payment required.

PLACEMENTS: Combined School of Computing CSE/IT rate for 2025-26 is 81.73%, with 555 placed out of 679 students and 307 internships. Not an AI-and-Edge-specific rate. Verified specialisation-specific average package unavailable. Historical university-wide INR 7.5 LPA brochure average must not be presented as this course average.

SCHOLARSHIPS: Current university merit schemes, financial-need support, work-study, eligible staff-family concessions and external awards may apply after approval. Verified national/international competition or sports achievements may qualify for 10% academic-year tuition waiver, subject to caps and conditions. Merit thresholds, renewal and EWS approval require confirmation.

Detailed current specialisation syllabus was not verified in this research. No guaranteed admission, award, employment or salary. Checked 2026-10-10.'),

(3, 'B.Tech CSE - Cyber Security and Forensics', 'COURSE',
'MIT-ADT University, Pune. School of Computing. Officially listed CSE specialisation: Cyber Security and Forensics.

FEES: Published common CSE category for 2026-27 intake: INR 287000, 315000, 335000 and 335000 across four years; total INR 1272000; approved-EWS total INR 732500. A separate specialisation fee row was not found; confirm applicability. Hostel/mess separate.

ELIGIBILITY: HSC/12th or equivalent with Physics and Mathematics plus Chemistry, Biotechnology, Biology or Technical Vocational subject; 50% in specified subject group, or 45% for applicable Maharashtra backward-class/PwD candidates. IB has separate conditions.

SELECTION: Maharashtra merit through MHT-CET 2026; All India through JEE Main 2026. Other state engineering tests, Uni-GAUGE or PERA may be considered for vacancies after CET/JEE applications. Published application fee INR 1800. Documents, selection, original verification and fee payment required.

PLACEMENTS: School of Computing combined CSE/IT 2025-26: 679 students, 555 placed, 81.73% placement, 307 internships. These are not Cyber Security-specific statistics. Verified course-specific average package unavailable. Historical university brochure average INR 7.5 LPA has unverified cohort year and must not be quoted as this course average.

SCHOLARSHIPS: University merit, verified financial-need support, Earn and Learn, eligible staff-family concessions and external awards, subject to approval. Verified competition/sports achievements may qualify for 10% relevant-year tuition waiver, subject to caps and conditions. Exact merit thresholds and renewal conditions require confirmation; EWS is conditional.

Detailed current syllabus was not verified in this research. No admission, award, placement or salary guarantee. Checked 2026-10-10.'),

(4, 'B.Tech CSE - Data Engineering', 'COURSE',
'MIT-ADT University, Pune. School of Computing. Officially listed CSE specialisation: Data Engineering.

FEES: Common published CSE fee category for 2026-27 intake: Year 1 INR 287000; Year 2 INR 315000; Year 3 INR 335000; Year 4 INR 335000; total INR 1272000; approved-EWS total INR 732500. Separate Data Engineering fee row not found; confirm applicability. Hostel/mess separate.

ELIGIBILITY: HSC/12th or equivalent with Physics and Mathematics plus Chemistry, Biotechnology, Biology or Technical Vocational subject. Minimum 50% in specified subjects; 45% for applicable Maharashtra backward-class/PwD candidates. IB has separate conditions.

SELECTION: Maharashtra merit through MHT-CET 2026; All India through JEE Main 2026. Other state entrance tests, Uni-GAUGE or PERA may be considered for remaining vacancies after CET/JEE applications. Published application fee INR 1800. Application, documents, selection, verification and fee payment required.

PLACEMENTS: School of Computing combined CSE/IT 2025-26: 555 placed out of 679 students; 81.73%; 307 internships. Not Data Engineering-specific. Verified course-specific average package unavailable. Historical university-wide brochure average INR 7.5 LPA is not a current Data Engineering figure.

SCHOLARSHIPS: Current university merit schemes, financial-need tuition support, work-study, eligible staff-family concessions and external awards require approval. Verified competition/sports achievements may receive 10% relevant-year tuition waiver, subject to caps and conditions. Confirm merit thresholds, renewal and EWS eligibility.

Detailed current syllabus was not verified in this research. No admission, scholarship, job or salary guarantee. Checked 2026-10-10.'),

(5, 'B.Tech Artificial Intelligence - Machine Learning', 'COURSE',
'MIT-ADT University, Pune. School of Artificial Intelligence. Programme: B.Tech Artificial Intelligence - Machine Learning. School states establishment in 2026-27.

PROGRAMME OVERVIEW: Published overview includes deep learning, neural networks, natural language processing, computer vision, model deployment and MLOps. School describes application areas in healthcare, financial technology and agriculture. This overview is not a complete semester-wise syllabus.

FEES: Published School of AI B.Tech category for 2026-27 intake: Year 1 INR 287000; Year 2 INR 315000; Year 3 INR 335000; Year 4 INR 335000; total INR 1272000; approved-EWS total INR 732500. These are school-level B.Tech fees; confirm selected specialisation applicability. Hostel/mess separate.

ELIGIBILITY AND SELECTION: Programme-specific rules were not verified. Confirm with School of AI instead of automatically applying another school rules.

PLACEMENTS: No verified graduating-cohort placement rate or average package found for this new school. School of Computing results must not be used as School of AI results. Career focus does not mean guaranteed placement. Historical university INR 7.5 LPA brochure average is not an AI-ML course outcome.

SCHOLARSHIPS: University 2026-27 ordinance includes merit schemes, verified financial-need support, Earn and Learn, eligible staff-family concessions and external scholarships. Verified competition/sports achievements may qualify for 10% relevant-year tuition waiver, subject to caps and conditions. Awards/EWS require approval; thresholds and renewal require confirmation.

No admission, scholarship, placement or salary guarantee. Checked 2026-10-10.'),

(6, 'B.Tech Electronics and Computer Engineering', 'COURSE',
'MIT-ADT University, Pune. School of Engineering and Sciences. Officially listed programme: Electronics and Computer Engineering.

FEES: Central Indian-resident 2026-27 intake schedule: Year 1 INR 247000; Year 2 INR 253000; Year 3 INR 270000; Year 4 INR 280000; total INR 1050000; approved-EWS total INR 596000. Published heading groups Electronics and Communication with Electronics and Computer Engineering. Hostel/mess separate. Older departmental tables may refer to earlier intake cohorts.

ELIGIBILITY: Published department rules specify 12th PCM/technical vocational subject group with English; minimum 50% open category or 45% applicable reserved category, with required category documents.

SELECTION: Department lists MHT-CET, JEE Main, other state engineering entrance examinations, Uni-GAUGE and PERA for 2026. Published application fee INR 1800. Application, documents, selection, verification and payment required.

PLACEMENTS: Verified current programme-specific placement rate and average package unavailable. Historical selected student offer records do not establish a current cohort average. University historical INR 7.5 LPA brochure average is not this course average.

SCHOLARSHIPS: University merit schemes, financial-need tuition support, work-study, eligible staff-family concessions and external awards may apply after approval. Verified competition/sports achievements may receive 10% relevant-year tuition waiver, subject to caps and conditions. Confirm thresholds, renewal and EWS approval.

Detailed current syllabus not verified in this research. No guaranteed admission, award, placement or salary. Checked 2026-10-10.'),

(7, 'B.Tech Mechanical Engineering', 'COURSE',
'MIT-ADT University, Pune. School of Engineering and Sciences. Officially listed programme: Mechanical Engineering.

FEES: Central Indian-resident 2026-27 intake schedule: Year 1 INR 247000; Year 2 INR 255000; Year 3 INR 276000; Year 4 INR 282000; total INR 1060000; approved-EWS total INR 607500. Published fee heading groups Mechanical with Aerospace and Robotics and Artificial Intelligence. Hostel/mess separate. Older departmental tables may describe earlier cohorts.

ELIGIBILITY: Published department rules specify 12th PCM/technical vocational subject group with English; 50% open category or 45% applicable reserved category, with required documents.

SELECTION: Listed examinations for 2026: MHT-CET, JEE Main, other state engineering examinations, Uni-GaUGE and PERA. Published application fee INR 1800. Application, documents, selection, verification and payment required.

PLACEMENTS: Verified current Mechanical-specific placement rate and average package unavailable. Selected historical offer records are not a complete cohort average. Historical university INR 7.5 LPA brochure average must not be described as Mechanical average.

SCHOLARSHIPS: Current university merit schemes, verified financial-need support, work-study, eligible staff-family concessions and external awards require approval. Verified competition/sports achievements may qualify for 10% relevant-year tuition waiver, subject to caps and conditions. Confirm thresholds, renewal and EWS conditions.

Detailed current syllabus not verified in this research. No admission, scholarship, placement or salary guarantee. Checked 2026-10-10.'),

(8, 'B.Tech Civil Engineering', 'COURSE',
'MIT-ADT University, Pune. School of Engineering and Sciences. Officially listed programme: Civil Engineering.

FEES: Central Indian-resident 2026-27 intake schedule: Year 1 INR 203000; Year 2 INR 207000; Year 3 INR 220000; Year 4 INR 230000; total INR 860000; approved-EWS total INR 501000. Hostel/mess separate. Older departmental tables may refer to earlier intake cohorts.

ELIGIBILITY: Published department rules specify 12th PCM/technical vocational subject group with English; minimum 50% open category or 45% applicable reserved category, with required documents.

SELECTION: Department lists MHT-CET, JEE Main, other state engineering examinations, Uni-GaUGE and PERA for 2026. Published application fee INR 1800. Application, documents, selection, original verification and payment required.

PLACEMENTS: Verified current Civil-specific placement rate and average package unavailable. Historical individual offers do not establish a current cohort average. Historical university brochure INR 7.5 LPA average is not Civil-specific.

SCHOLARSHIPS: University merit schemes, verified financial-need tuition support, work-study, eligible staff-family concessions and external awards require approval. Verified competition/sports achievements may qualify for 10% relevant-year tuition waiver, subject to caps and conditions. Confirm merit thresholds, renewal and EWS conditions.

Detailed current syllabus not verified in this research. No guaranteed admission, scholarship, employment or salary. Checked 2026-10-10.'),

(9, 'Bachelor of Computer Applications (BCA)', 'COURSE',
'MIT-ADT University, Pune. School of Business and Computer Applications. BCA is officially listed. Inspected current programme page says detailed programme information is coming soon.

FEES: Published Indian-resident 2026-27 row: Year 1 INR 130000; Year 2 INR 140000; Year 3 INR 85000; total INR 355000. Hostel/mess separate.

SOURCE INCONSISTENCY: Published EWS row lists four annual amounts and a higher total than the regular row. Do not quote EWS fees, infer degree duration or explain the discrepancy without admissions clarification.

ELIGIBILITY: Detailed current BCA qualification/minimum marks were not verified on the inspected programme page. Admissions document checklist includes BBA/BCA CET score card. Confirm current qualification, marks, accepted examination and degree route with admissions.

PLACEMENTS: Verified current BCA-specific placement rate and average package unavailable. Do not substitute School of Computing or university-wide statistics.

SCHOLARSHIPS: University 2026-27 merit schemes, verified financial-need tuition support, work-study, eligible staff-family concessions and external awards may apply after approval. Verified competition/sports achievements may qualify for 10% relevant-year tuition waiver, subject to caps and conditions. Exact thresholds/renewal require confirmation.

No admission, award, placement or salary guarantee. Checked 2026-10-10.'),

(10, 'Bachelor of Business Administration (BBA)', 'COURSE',
'MIT-ADT University, Pune. School of Business and Computer Applications. BBA offers business specialisations; requirements depend on selected track.

FEES: Published general BBA Indian-resident 2026-27 row: Year 1 INR 210000; Year 2 INR 220000; Year 3 INR 233500; Year 4 INR 150000; total INR 813500; approved-EWS total INR 483000. Hostel/mess separate. BBA Finance aligned/accredited with UK ACCA has a separate fee category.

SOURCE INCONSISTENCY: Central fee table lists four annual amounts while inspected BBA specialisation pages describe three-year programmes. Confirm degree/exit/honours route, duration and actual payable total before quoting.

ELIGIBILITY: Global/International Business page specifies 10+2/HSC or equivalent with 50%, or 45% applicable reserved category, plus an accepted common entrance examination. Confirm selected-track criteria.

ADMISSION: Register, apply, submit documents, complete applicable entrance/evaluation, undergo selection, verify documents and pay fees.

PLACEMENTS: Verified current BBA-specific placement rate and average package unavailable. Do not substitute university or computing-school figures.

SCHOLARSHIPS: University merit schemes, verified financial-need tuition support, work-study, eligible staff-family concessions and external awards require approval. Verified competition/sports achievements may qualify for 10% relevant-year tuition waiver, subject to caps and conditions. Confirm thresholds, renewal and EWS eligibility.

No admission, scholarship, placement or salary guarantee. Checked 2026-10-10.'),

(11, 'Hostel', 'HOSTEL',
'MIT-ADT University, Pune. Separate boys and girls accommodation, including on-campus and off-campus facilities.

FACILITIES: Published facilities include Wi-Fi, RO drinking water, solar hot water, indoor games, laundry, power supply, hostel staff and round-the-clock security. Vegetarian/Jain mess options. Confirm specific services and charges for allotted building.

ALLOTMENT: First come first served, valid for one academic year. Availability requires confirmation. Course admission does not automatically reserve a room.

PROCESS: Apply through TCSiON Student Portal, complete hostel application and pay full academic-year fees. Final confirmation is emailed after successful payment. Mess and applicable ancillary services are mandatory. Annual willingness is required for retention. Published rules do not permit mid-term vacation of accommodation.

FEES: Verified current annual hostel tariff and deposit unavailable. Obtain building/room-wise hostel, mess, deposit and ancillary charges from hostel office. Older approximately INR 130000 FAQ estimate must not be quoted as current.

REFUNDS: Obtain revised cancellation/refund policy from hostel office; do not promise refund amounts.

CONTACT: Hostel enquiries 020 6765 3138 / 3139; hostel.inquiry@mituniversity.edu.in. Fee enquiries 020 6765 3137; hostel.fees@mituniversity.edu.in.

Offline admission appointment booking does not reserve hostel accommodation. Checked 2026-10-10.'),

(12, 'Scholarships', 'SCHOLARSHIP',
'MIT-ADT University scholarship ordinance effective academic year 2026-27.

MERIT SCHEMES: Merit Excellence, Merit Achiever, Merit Performer and Merit-Based Support.

OTHER SUPPORT: Verified financial-need tuition support; Earn and Learn work-study; eligible MIT Group staff ward/spouse concessions; notified school/programme-specific schemes; external/private sponsor scholarships.

ACHIEVEMENT SUPPORT: Fixed 10% waiver of relevant academic-year tuition for verified national/international competition or sports achievements, including Smart India Hackathon. Subject to annual caps and conditions. Not 10% of total programme cost, hostel or mess fees.

GOVERNMENT/EWS: Scholarships, freeships and statutory concessions depend on government notifications, institution eligibility, portal rules, required documents and approval. Published EWS fee rows are conditional approved categories, not automatic discounts.

Exact current merit thresholds, award rates and renewal conditions were not verified. Do not invent them. An older engineering page mentions limited EWS 50% tuition support; do not present it as an unconditional 2026-27 entitlement.

Do not assume awards can be combined, renewed automatically or applied to hostel charges. Checked 2026-10-10.'),

(13, 'School of Computing Placements', 'PLACEMENT',
'MIT-ADT School of Computing. Scope: Computer Science Engineering and Information Technology combined.

Published academic year 2025-26: total students 679; placed students 555; placement rate 81.73%; internships 307.

Historical published placement rates: 2024-25 80.2%; 2023-24 90.1%.

Rate is published against total students; eligible/participating-student denominator is not separately specified. Verified school/cohort average package unavailable.

Always state school scope and year. Not an individual CSE specialisation rate. Not applicable as a placement result for School of AI, BCA, BBA, Mechanical or Civil. Internship counts are separate; do not add them to placed-student counts. No future employment guarantee. Checked 2026-10-10.'),

(14, 'University Placement Overview', 'PLACEMENT',
'Historical MIT-ADT university promotional brochure with a 2024 filename reports average package INR 7.5 lakh per annum.

Scope: University-wide historical brochure claim. Exact graduating cohort year unverified. Current 2026 programme-specific average not established.

Only describe this as a historical brochure figure. Do not present it as current average for any individual course or an expected student salary.

Official engineering/management documents include individual offer records, but selected records do not establish current course placement rates or audited cohort averages. Placement rate and average package are distinct metrics. No employment or salary guarantee. Checked 2026-10-10.'),

(15, 'Admission Process', 'ADMISSION',
'MIT-ADT admissions information inspected for academic year 2026-27.

COMPUTING ELIGIBILITY: HSC/12th or equivalent with Physics and Mathematics plus Chemistry, Biotechnology, Biology or Technical Vocational subject; minimum 50% in specified subject group or 45% applicable Maharashtra backward-class/PwD candidates. IB has separate conditions.

COMPUTING SELECTION: Maharashtra merit through MHT-CET 2026; All India through JEE Main 2026. Other state tests, Uni-GaUGE or PERA may be considered for vacancies after CET/JEE applications.

ENGINEERING AND SCIENCES: Published department rules specify 12th PCM/technical vocational subject group with English; 50% open category or 45% applicable reserved category, with required documents. Listed examinations include MHT-CET, JEE Main, other state engineering tests, Uni-GaUGE and PERA for 2026.

Published application fee for inspected computing/engineering departments INR 1800.

PROCESS: Application, required documents, applicable merit/selection, provisional decision, original-document verification and required fee payment.

BCA/BBA and School of AI require their own programme-specific checks. Do not automatically apply engineering requirements.

Minimum eligibility does not guarantee admission. Current cutoffs, deadlines and vacant seats were not verified. Offline counselling appointment does not confirm admission. Available meeting dates/times must come from booking backend, not invented resource data. Checked 2026-10-10.')

ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    category = VALUES(category),
    description = VALUES(description);

COMMIT;

select * from resource;