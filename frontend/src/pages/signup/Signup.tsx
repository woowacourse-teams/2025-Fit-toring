import AuthFooter from './components/AuthFooter/AuthFooter';
import SignupHeader from './components/SignupHeader/SignupHeader';
import SignupIntro from './components/SignupIntro/SignupIntro';

function Signup() {
  return (
    <div>
      <SignupHeader />
      <SignupIntro />
      <AuthFooter currentPage="signup" />
    </div>
  );
}

export default Signup;
